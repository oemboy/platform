
package net.platform.center.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import lombok.Builder;
import lombok.Data;

/**
 * 组合请求返回的信息
 *
 * @author Jack
 */
@Data
@Builder
public class ResponseComponent {
    private HttpHeaders headers;

    private HttpStatus status;

    private WebRequest request;
}
