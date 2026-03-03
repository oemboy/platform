
package net.platform.center.cfg.http5;

import static net.platform.center.exception.ExceptionEnum.ERR_COMMON_VALID;

import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.exception.BusinessException;
import net.platform.center.util.RestClientUtil;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */

@Slf4j
@AutoConfiguration
@ConditionalOnClass({HttpClient.class, RestClient.class})
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = "http", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HttpClientAutoConfiguration {

    private final HttpClientProperties properties;

    public HttpClientAutoConfiguration(HttpClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public PoolingHttpClientConnectionManager httpClientConnectionManager() {
        try {
            final PoolingHttpClientConnectionManagerBuilder builder =
                PoolingHttpClientConnectionManagerBuilder.create();

            final SSLContext sslContext =
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();

            DefaultClientTlsStrategy socketFactoryStrategy =
                new DefaultClientTlsStrategy(sslContext, NoopHostnameVerifier.INSTANCE);

            builder.setTlsSocketStrategy(socketFactoryStrategy);

            builder.setMaxConnPerRoute(properties.getMaxPerRoute());
            builder.setMaxConnTotal(properties.getMaxTotal());

            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setSocketTimeout(Timeout.ofSeconds(properties.getSocketTimeout()))
                .setConnectTimeout(Timeout.ofSeconds(properties.getConnectionTimeout()))
                .setTimeToLive(TimeValue.ofMinutes(10))
                .build();

            builder.setDefaultConnectionConfig(connectionConfig);
            return builder.build();

        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error("HttpClientConnectionManager build error:", e);
            throw new BusinessException(ERR_COMMON_VALID, e, e.getMessage());
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(properties.getConnectionTimeout()))
            .setResponseTimeout(Timeout.ofSeconds(properties.getSocketTimeout()))
            .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClient httpClient(PoolingHttpClientConnectionManager httpClientConnectionManager,
        RequestConfig requestConfig) {

        HttpClientBuilder builder = HttpClients.custom()
            .setConnectionManager(httpClientConnectionManager)
            .setDefaultRequestConfig(requestConfig);

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory,
        List<ClientHttpRequestInterceptor> interceptors) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        log.info("正在构建 RestTemplate, 当前转换器数量: {}", list.size());
        list.forEach(c -> log.info("RestTemplate转换器: {}", c.getClass().getSimpleName()));
        for (HttpMessageConverter<?> converter : list) {
            if (converter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
        moveLogInterceptor(interceptors);
        restTemplate.getInterceptors().addAll(interceptors);
        return restTemplate;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(RestClient.class)
    public RestClient restClient(ClientHttpRequestFactory clientHttpRequestFactory,
        List<ClientHttpRequestInterceptor> interceptors) {
        return RestClient.builder()
            .requestFactory(clientHttpRequestFactory)
            .requestInterceptors(list -> list.addAll(this.moveLogInterceptor(interceptors)))
            .messageConverters(list -> {
                for (HttpMessageConverter<?> converter : list) {
                    if (converter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
                    }
                }
                log.info("正在构建 RestClient, 当前转换器数量: {}", list.size());
                list.forEach(c -> log.info("RestClient转换器: {}", c.getClass().getSimpleName()));
            })
            .build();
    }

    @Bean("proxyRestClient")
    @ConditionalOnMissingBean(name = "proxyRestClient")
    public RestClient proxyRestClient(RestClient restClient, PoolingHttpClientConnectionManager poolingManager,
        RequestConfig requestConfig, ObjectProvider<ProxyProvider> provider) {
        // 这里传入的 provider 在请求时才会触发
        return RestClientUtil.custom(restClient, poolingManager, requestConfig, provider.getIfAvailable());
    }

    /**
     * 日志拦截器放到最后
     * 
     * @param interceptors 所有的拦截器
     */
    private List<ClientHttpRequestInterceptor> moveLogInterceptor(List<ClientHttpRequestInterceptor> interceptors) {
        if (interceptors == null || interceptors.isEmpty()) {
            return interceptors;
        }

        // 1. 检查最后一个元素是不是已经是我们要的日志拦截器
        if (interceptors.get(interceptors.size() - 1) instanceof HttpClientLogInterceptor) {
            return interceptors;
        }

        // 2. 如果最后一位不是，则去前面找并移动, 只处理一个日志拦截器,多个不支持
        HttpClientLogInterceptor logInterceptor = null;
        for (int i = 0; i < interceptors.size() - 1; i++) { // 只需找倒数第二个及以前的
            if (interceptors.get(i) instanceof HttpClientLogInterceptor) {
                logInterceptor = (HttpClientLogInterceptor) interceptors.remove(i);
                break;
            }
        }

        if (logInterceptor != null) {
            interceptors.add(logInterceptor);
        }
        return interceptors;
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientHttpRequestInterceptor requestResponseLoggingInterceptor() {
        return new HttpClientLogInterceptor();
    }

}
