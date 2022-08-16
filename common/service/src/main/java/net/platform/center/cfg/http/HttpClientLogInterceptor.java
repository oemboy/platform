
package net.platform.center.cfg.http;

import static net.platform.center.util.AntPathUtil.decodeUrl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

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
                // 应该优先取 header 包含的字符编码, 此处偷懒
                log.debug("URI         : {}", decodeUrl(request.getURI().toString()));
                log.debug("Method      : {}", request.getMethod());
                log.debug("Headers     : {}", request.getHeaders());
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
                HttpHeaders headers = response.getHeaders();
                log.debug("Headers      : {}", headers);
                // 优先取 response 字符编码
                InputStream stream = response.getBody();
                if (stream instanceof ByteArrayInputStream) {
                    log.debug("Response body: {}", StreamUtils.copyToString(stream, responseCharset(headers)));

                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.debug("===========================response end===========================");
        }
    }

    private Charset responseCharset(HttpHeaders headers) {
        try {
            return Optional.ofNullable(headers)
                .map(HttpHeaders::getContentType)
                .map(MediaType::getCharset)
                .orElse(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("responseCharset:", e);
        }
        return StandardCharsets.UTF_8;
    }
}
