
package net.platform.center.cfg.http;

import static net.platform.center.exception.ExceptionEnum.ERR_COMMON_VALID;

import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Lookup;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContextBuilder;
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
    public HttpClientConnectionManager httpClientConnectionManager() {
        try {
            SSLContext context = new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build();

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE))
                .build();
            PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            connectionManager.setDefaultMaxPerRoute(maxPerRoute);
            connectionManager.setMaxTotal(maxTotal);
            return connectionManager;

        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            log.error("HttpClientConnectionManager build error:", e);
            throw new BusinessException(ERR_COMMON_VALID, e, e.getMessage());
        }

    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
            .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
            .setSocketTimeout(socketTimeout * 1000)
            .setConnectTimeout(connectionTimeout * 1000)
            .build();

    }

    @Bean
    public HttpClient httpClient(HttpClientConnectionManager httpClientConnectionManager, RequestConfig requestConfig) {
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
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
        restTemplate.getInterceptors().addAll(interceptors);
        return restTemplate;
    }

    @Bean
    public HttpAsyncClient closeableHttpAsyncClient(RequestConfig requestConfig) throws IOReactorException {
        CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.custom()
            .setConnectionManager(buildAsyncMgr())
            .setDefaultAuthSchemeRegistry(buildAuthSchemeProvider())
            .setDefaultRequestConfig(requestConfig)
            .build();

        closeableHttpAsyncClient.start();
        return closeableHttpAsyncClient;
    }

    @Bean
    public ClientHttpRequestInterceptor requestResponseLoggingInterceptor() {
        return new HttpClientLogInterceptor();
    }

    private NHttpClientConnectionManager buildAsyncMgr() throws IOReactorException {
        Registry<SchemeIOSessionStrategy> schemeRegistry = RegistryBuilder.<SchemeIOSessionStrategy> create()
            .register("http", NoopIOSessionStrategy.INSTANCE)
            .register("https", SSLIOSessionStrategy.getDefaultStrategy())
            .build();

        int processors = Runtime.getRuntime().availableProcessors();
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(Math.max(processors, 16)).build();
        ConnectingIOReactor reactor = new DefaultConnectingIOReactor(ioReactorConfig);
        PoolingNHttpClientConnectionManager conMgr = new PoolingNHttpClientConnectionManager(reactor, schemeRegistry);
        conMgr.setMaxTotal(maxTotal);
        conMgr.setDefaultMaxPerRoute(maxPerRoute);

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
            .setMalformedInputAction(CodingErrorAction.IGNORE)
            .setUnmappableInputAction(CodingErrorAction.IGNORE)
            .setCharset(StandardCharsets.UTF_8)
            .build();

        conMgr.setDefaultConnectionConfig(connectionConfig);
        return conMgr;
    }

    private Lookup<AuthSchemeProvider> buildAuthSchemeProvider() {
        return RegistryBuilder.<AuthSchemeProvider> create()
            .register(AuthSchemes.BASIC, new BasicSchemeFactory())
            .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
            .register(AuthSchemes.NTLM, new NTLMSchemeFactory())
            .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
            .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
            .build();
    }

}
