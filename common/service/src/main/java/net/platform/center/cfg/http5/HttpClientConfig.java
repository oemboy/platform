
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
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.platform.center.exception.BusinessException;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */

@Configuration
@Slf4j
@Data
@ConfigurationProperties(prefix = "http")
public class HttpClientConfig {
    private Integer maxTotal = 500;

    private Integer maxPerRoute = 300;

    private Integer connectionTimeout = 10;

    private Integer connectionRequestTimeout = 150;

    private Integer socketTimeout = 120;

    @Bean
    public PoolingHttpClientConnectionManager httpClientConnectionManager() {
        try {
            final PoolingHttpClientConnectionManagerBuilder builder =
                PoolingHttpClientConnectionManagerBuilder.create();
            final SSLContext sslContext =
                new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();

            SSLConnectionSocketFactory socketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                // .setTlsVersions(TLS.V_1_1, TLS.V_1_2, TLS.V_1_3
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

            builder.setSSLSocketFactory(socketFactory);

            builder.setMaxConnPerRoute(maxPerRoute);
            builder.setMaxConnTotal(maxTotal);

            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setSocketTimeout(Timeout.ofSeconds(socketTimeout))
                .setConnectTimeout(Timeout.ofSeconds(connectionRequestTimeout))
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
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(connectionRequestTimeout))
            .setResponseTimeout(Timeout.ofSeconds(socketTimeout))
            .build();

    }

    @Bean
    public HttpClient httpClient(PoolingHttpClientConnectionManager httpClientConnectionManager,
        RequestConfig requestConfig) {
        return HttpClients.custom()
            .setConnectionManager(httpClientConnectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();

    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory,
        List<ClientHttpRequestInterceptor> interceptors) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : list) {
            if (converter instanceof StringHttpMessageConverter stringHttpMessageConverter) {
                stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        }

        moveLogInterceptor(interceptors);
        restTemplate.getInterceptors().addAll(interceptors);
        return restTemplate;
    }

    /**
     * 日志拦截器放到最后
     * 
     * @param interceptors 所有的拦截器
     */
    private void moveLogInterceptor(List<ClientHttpRequestInterceptor> interceptors) {
        HttpClientLogInterceptor logInterceptor = null;
        for (int i = 0; i < interceptors.size() - 1; i++) {
            if (interceptors.get(i) instanceof HttpClientLogInterceptor) {
                logInterceptor = (HttpClientLogInterceptor) interceptors.get(i);
                interceptors.remove(i);
                break;
            }
        }

        if (logInterceptor != null) {
            interceptors.add(logInterceptor);
        }
    }

    @Bean
    public ClientHttpRequestInterceptor requestResponseLoggingInterceptor() {
        return new HttpClientLogInterceptor();
    }

}
