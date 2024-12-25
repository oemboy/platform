
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.platform.center.bean.CommonResp;
import net.platform.center.util.ExceptionUtil;
import net.platform.center.util.JsonUtil;

/**
 * 全局异常处理, 继承{@linkplain ResponseEntityExceptionHandler}。<br/>
 * 增强{@linkplain ResponseEntityExceptionHandler#handleMethodArgumentNotValid(
 * MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request)}
 * 处理， 处理见{@linkplain GlobalExceptionHandler#handleMethodArgumentNotValid(
 * MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request)}。<br/>
 * 基本异常处理见{@linkplain GlobalExceptionHandler#exception(Exception, HttpHeaders, HttpStatus, WebRequest)}。<br/>
 *
 * @author Jack
 * @since 2021/9/5
 */
@Slf4j
@ControllerAdvice
@Configurable(preConstruction = true)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private List<ExceptionCustomizer> customizers;

    public List<ExceptionCustomizer> getCustomizers() {
        return customizers;
    }

    public void setCustomizers(@Autowired(required = false) List<ExceptionCustomizer> customizers) {
        this.customizers = customizers;
    }

    /**
     * 处理逻辑见{@linkplain GlobalExceptionHandler#exception(Exception, HttpHeaders, HttpStatus, WebRequest)}
     *
     * @param ex 异常对象
     * @param body 响应体对象
     * @param headers Http响应请求头信息
     * @param statusCode Http响应状态码
     * @param request Spring封装的请求对象
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers,
        HttpStatusCode statusCode, WebRequest request) {
        return exception(ex, headers, HttpStatus.OK, request);
    }

    /**
     * {@linkplain MethodArgumentNotValidException 参数校验异常} 需要特殊处理,
     * 将校验不通过字段的异常详细信息转换成 {@linkplain ExceptionEnum#ERR_PARAM_VALID} 再放回给调用方。
     *
     * @param ex 参数校验异常
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatusCode status, WebRequest request) {
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

    /**
     * 处理全部异常, 处理逻辑见{@linkplain GlobalExceptionHandler#exception(Exception, HttpHeaders, HttpStatus, WebRequest)}
     *
     * @param ex 异常信息
     * @param request 当前请求信息
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
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

    /**
     * 兼容处理校验框架的 ConstraintViolationException
     *
     * @param ex 校验异常
     * @param request 当前请求对象
     * @return 参数校验处理异常
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return this.exception(new BusinessException(ExceptionEnum.ERR_PARAM_VALID,
            ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","))),
            new HttpHeaders(), HttpStatus.OK, request);
    }

    /**
     * 处理异常信息， 转为为{@linkplain ResponseEntity}对象， 以便统一异常处理的返回。<br/>
     * {@linkplain ResponseEntity#getBody()} 统一转化为 {@linkplain CommonResp}。<br/>
     * {@linkplain MethodArgumentNotValidException}异常需要特殊处理，
     * 详情见{@linkplain GlobalExceptionHandler#handleMethodArgumentNotValid(
     * MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request)}.<br/>
     * {@linkplain BusinessException} 业务异常可以直接转化成 {@linkplain CommonResp}。<br/>
     * 其他异常则返回 {@linkplain ExceptionEnum#ERROR}.
     *
     * @param ex 处理的异常， 包含业务异常
     * @param headers Http响应请求头信息
     * @param status Http响应状态码
     * @param request Spring封装的请求对象
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
    protected ResponseEntity<Object> exception(Exception ex, HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        final ResponseComponent respComp = ResponseComponent.builder()
            .status(ObjectUtils.defaultIfNull(status, HttpStatus.OK))
            .headers(ObjectUtils.defaultIfNull(headers, new HttpHeaders()))
            .request(request)
            .build();
        if (Objects.isNull(ex)) {
            return buildResponseEntity(ERROR, respComp);
        }

        log.error("Processing failed", ex);
        ExceptionCode errCode = ex instanceof BusinessException exception ? exception : ERROR;
        return buildResponseEntity(errCode, respComp);
    }

    /**
     * 处理敏感异常, 默认转化成{@linkplain ExceptionEnum#ERROR}.
     *
     * @param ex 敏感异常
     * @return {@linkplain ExceptionCode 异常信息}
     * @see ExceptionUtil#isSensitiveException(Throwable)
     */
    protected ExceptionCode handleSensitiveException(Exception ex) {
        log.error("handle sensitive exception {}", ex.getClass());
        return ERROR;
    }

    /**
     * 处理字段异常, 默认转化成{@linkplain ExceptionEnum#ERR_PARAM_VALID}.
     *
     * @param allFieldErr 字段异常数组
     * @return {@linkplain ExceptionCode 异常信息}
     */
    protected ExceptionCode handleFieldError(List<FieldError> allFieldErr) {
        final String errMsg = allFieldErr.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
        return new BusinessException(ERR_PARAM_VALID, errMsg);
    }

    /**
     * 将{@linkplain ExceptionCode 异常信息}转化成{@linkplain ResponseEntity Spring响应实体对象},
     * 其中{@linkplain ExceptionCode 异常信息}转化成 {@linkplain CommonResp}.<br/>
     *
     * @param errCode 异常信息
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
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
        return ResponseEntity.status(ObjectUtils.defaultIfNull(respComp.getStatus(), HttpStatus.OK))
            .headers(respComp.getHeaders())
            .body(respBody);
    }
}
