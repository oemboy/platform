
package net.platform.center.exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.platform.center.bean.CommonResp;
import net.platform.center.util.JsonUtil;
import net.platform.center.util.LogUtil;

/**
 * @author Jack
 * @since 2021/09/03
 */
@Slf4j
public final class ExceptionAssistant {
    private static final String APPLICATION_JSON_UTF8 = "application/json;charset=utf-8";

    private ExceptionAssistant() {
    }

    /**
     * 提供处理完成异常信息后直接回写调用方能力. <br/>
     *
     * @param response 响应对象
     * @param ex 需要处理的异常
     * @param contentType 返回contentType
     * @throws IOException 输出给调用方可能会出现异常
     */
    public static void writeResponse(HttpServletResponse response, Exception ex, String contentType, int status)
        throws IOException {
        if (Objects.isNull(response) || Objects.isNull(ex)) {
            log.warn("HttpServletResponse is null or Exception is null, Unable to process exception message");
            return;
        }

        log.error("处理异常", ex);
        ExceptionCode errCode = ex instanceof ExceptionCode ec ? ec : ExceptionEnum.ERROR;
        final CommonResp body = new CommonResp();
        body.setCode(errCode.getCode());
        body.setMessage(errCode.getMessage());
        body.setLogId(LogUtil.getLogId());
        ResponseEntity<Object> entity = ResponseEntity.ok(body);
        HttpHeaders headers = entity.getHeaders();
        headers.forEach((key, values) -> {
            if (!values.isEmpty()) {
                // 使用 String.join 或 Collectors 逻辑
                String headerValue = values.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(", "));
                if (StringUtils.isNotEmpty(headerValue)) {
                    response.setHeader(key, headerValue);
                }
            }
        });
        response.setStatus(status);
        response.setContentType(contentType);
        response.getOutputStream()
            .write(StringUtils.trimToEmpty(JsonUtil.toJsonString(entity.getBody())).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 提供处理完成异常信息后直接回写调用方能力. <br/>
     *
     * @param response 响应对象
     * @param ex 需要处理的异常
     * @throws IOException 输出给调用方可能会出现异常
     */
    public static void writeResponse(HttpServletResponse response, Exception ex, String contentType)
        throws IOException {
        writeResponse(response, ex, contentType, HttpStatus.OK.value());
    }

    /**
     * 提供处理完成异常信息后直接回写调用方能力. <br/>
     *
     * @param response 响应对象
     * @param ex 需要处理的异常
     * @throws IOException 输出给调用方可能会出现异常
     */
    public static void writeResponse(HttpServletResponse response, Exception ex) throws IOException {
        writeResponse(response, ex, APPLICATION_JSON_UTF8, HttpStatus.OK.value());
    }
}
