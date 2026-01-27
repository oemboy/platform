
package net.platform.center.util;

import java.net.InetSocketAddress;
import java.net.ProxySelector;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import net.platform.center.cfg.http5.ProxyProvider;

/**
 * @author bing zi
 * @since 2026/1/27
 */

public final class RestClientUtil {
    private RestClientUtil() {
    }

    /**
     * 变异出一个具有特定 代理、超时、Cookie 的 RestClient
     * * @param baseClient 原始 RestClient Bean
     * 
     * @param poolingManager 原始连接池管理器 (关键：确保共用)
     * @param defaultRequestConfig 默认请求配置
     * @param proxyProvider 本次特定的代理提供者 (传 null 则直连)
     * @param requestConfig 特定超时配置 (传 null 则默认)
     * @param cookieStore 特定 Cookie 存储 (传 null 则默认)
     */
    public static RestClient custom(RestClient baseClient, PoolingHttpClientConnectionManager poolingManager,
        RequestConfig defaultRequestConfig, ProxyProvider proxyProvider, RequestConfig requestConfig,
        CookieStore cookieStore) {
        // 1. 构建一个临时的 HttpClient，但核心是复用 poolingManager
        HttpClientBuilder builder = HttpClients.custom()
            .setConnectionManager(poolingManager)
            // 标记：不要在关闭 HttpClient 时关闭连接池
            .setConnectionManagerShared(true)
            .setDefaultRequestConfig(defaultRequestConfig);
        // 2. 注入特定的 Proxy 逻辑
        if (proxyProvider != null) {
            builder.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()) {
                @Override
                protected HttpHost determineProxy(HttpHost target, HttpContext context) {
                    InetSocketAddress addr = proxyProvider.getProxyAddress();
                    if (addr != null) {
                        return new HttpHost(addr.getHostName(), addr.getPort());
                    }
                    return null;
                }
            });
        }
        CloseableHttpClient customHttpClient = builder.build();
        HttpComponentsClientHttpRequestFactory factory = getFactory(requestConfig, cookieStore, customHttpClient);
        // 4. 返回变异后的客户端
        return baseClient.mutate().requestFactory(new BufferingClientHttpRequestFactory(factory)).build();
    }

    private static HttpComponentsClientHttpRequestFactory getFactory(RequestConfig requestConfig,
        CookieStore cookieStore, CloseableHttpClient customHttpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(customHttpClient);
        // 3. 配置 RequestFactory 和 HttpContext (处理超时和 Cookie)
        if (requestConfig != null || cookieStore != null) {
            factory.setHttpContextFactory((method, uri) -> {
                HttpClientContext context = HttpClientContext.create();
                if (requestConfig != null) {
                    context.setRequestConfig(requestConfig);
                }
                if (cookieStore != null) {
                    context.setCookieStore(cookieStore);
                }
                return context;
            });
        }
        return factory;
    }

    /**
     * 生成一个具有使用代理功能的RestClient
     * 
     * @param restClient 原始的 RestClient Bean
     * @param poolingManager PoolingHttpClientConnectionManager
     * @param requestConfig RequestConfig
     * @param proxyProvider ProxyProvider
     * @return 定制后的 RestClient
     */
    public static RestClient custom(RestClient restClient, PoolingHttpClientConnectionManager poolingManager,
        RequestConfig requestConfig, ProxyProvider proxyProvider) {
        return custom(restClient, poolingManager, requestConfig, proxyProvider, null, null);
    }
}
