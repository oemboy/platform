
package net.platform.center.cfg.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: bing zi
 * @Date: 2021/8/31
 */

@Slf4j
@SuppressWarnings("NullableProblems")
public class HttpClientLogInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);

        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            try {
                log.debug("===========================request begin===========================");
                log.debug("URI         : {}", request.getURI());
                log.debug("Method      : {}", request.getMethod());
                log.debug("Headers     : {}", request.getHeaders());
                // TODO 优先取 header 包含的字符编码
                log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.debug("==========================request end===========================");

        }
    }

    private void logResponse(ClientHttpResponse response) {
        if (log.isDebugEnabled()) {
            try {
                log.debug("===========================response begin===========================");
                log.debug("Status code  : {}", response.getStatusCode());
                log.debug("Status text  : {}", response.getStatusText());
                log.debug("Headers      : {}", response.getHeaders());
                // 优先取 response 字符编码
                // 流会耗尽吗??
                // log.debug("Response body: {}", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            log.debug("===========================response end===========================");
        }
    }

}
