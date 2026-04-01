
package net.platform.center.exception;

import static net.platform.center.exception.ExceptionEnum.ERROR;
import static net.platform.center.exception.ExceptionEnum.ERR_PARAM_VALID;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.platform.center.bean.CommonResp;
import net.platform.center.util.JsonUtil;

@Slf4j
@ControllerAdvice
@Configurable(preConstruction = true)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Getter
    private List<ExceptionCustomizer> customizers;

    public void setCustomizers(@Autowired(required = false) List<ExceptionCustomizer> customizers) {
        this.customizers = customizers;
    }

    @Override
    protected @Nullable ResponseEntity<@NonNull Object> handleExceptionInternal(@NonNull Exception ex,
        @Nullable Object body, @NonNull HttpHeaders headers, @NonNull HttpStatusCode statusCode,
        @NonNull WebRequest request) {
        return exception(ex, headers, HttpStatus.OK, request);
    }

    @Override
    protected @Nullable ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        log.error("Processing method argument not valid", ex);
        final List<FieldError> allFieldErr = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .filter(FieldError.class::isInstance)
            .map(FieldError.class::cast)
            .toList();

        final ResponseComponent respComp =
            ResponseComponent.builder().status(HttpStatus.OK).headers(headers).request(request).build();
        if (CollectionUtils.isEmpty(allFieldErr)) {
            return buildResponseEntity(ERROR, respComp);
        }
        return buildResponseEntity(handleFieldError(allFieldErr), respComp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex, WebRequest request) {
        return exception(ex, new HttpHeaders(), HttpStatus.OK, request);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Object> webExchangeBindException(WebExchangeBindException ex) {
        return this.exception(
            new BusinessException(net.platform.center.exception.ExceptionEnum.ERR_PARAM_VALID,
                ex.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","))),
            new HttpHeaders(), HttpStatus.OK, null);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return this.exception(new BusinessException(ExceptionEnum.ERR_PARAM_VALID,
            ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","))),
            new HttpHeaders(), HttpStatus.OK, request);
    }

    protected ResponseEntity<Object> exception(Exception ex, HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        final ResponseComponent respComp = ResponseComponent.builder()
            .status(ObjectUtils.getIfNull(status, HttpStatus.OK))
            .headers(ObjectUtils.getIfNull(headers, new HttpHeaders()))
            .request(request)
            .build();
        if (Objects.isNull(ex)) {
            return buildResponseEntity(ERROR, respComp);
        }

        log.error("Processing failed", ex);
        ExceptionCode errCode = ex instanceof BusinessException exception ? exception : ERROR;
        return buildResponseEntity(errCode, respComp);
    }

    protected ExceptionCode handleSensitiveException(Exception ex) {
        log.error("handle sensitive exception {}", ex.getClass());
        return ERROR;
    }

    protected ExceptionCode handleFieldError(List<FieldError> allFieldErr) {
        final String errMsg = allFieldErr.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
        return new BusinessException(ERR_PARAM_VALID, errMsg);
    }

    protected ResponseEntity<Object> buildResponseEntity(ExceptionCode errCode, ResponseComponent respComp) {
        // 自定义错误处理
        final Optional<ExceptionCustomizer> customizerOpt =
            ListUtils.emptyIfNull(getCustomizers()).stream().filter(c -> c.support(respComp)).findFirst();

        Object respBody = null;
        if (customizerOpt.isPresent()) {
            respBody = customizerOpt.get().customize(errCode, respComp);
        }

        final String code = errCode.getCode();
        final String message = errCode.getMessage();
        if (Objects.isNull(respBody)) {
            final CommonResp resp = new CommonResp();
            resp.setCode(code);
            resp.setMessage(message);
            respBody = resp;
        }
        log.error("Response Exception code:{}, message:{}, body:{}", code, message, JsonUtil.toJson(respBody));
        return ResponseEntity.status(ObjectUtils.getIfNull(respComp.getStatus(), HttpStatus.OK))
            .headers(respComp.getHeaders())
            .body(respBody);
    }
}
