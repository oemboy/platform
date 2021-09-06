package net.platform.center.exception;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.bean.CommonResp;
import net.platform.center.util.ExceptionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.platform.center.exception.ExceptionEnum.ERROR;
import static net.platform.center.exception.ExceptionEnum.ERR_PARAM_VALID;

/**
 * 全局异常处理, 继承{@linkplain ResponseEntityExceptionHandler}。<br/>
 * 增强{@linkplain ResponseEntityExceptionHandler#handleMethodArgumentNotValid(
 *MethodArgumentNotValidException, HttpHeaders, HttpStatus, WebRequest)}
 * 处理， 处理见{@linkplain GlobalExceptionHandler#handleMethodArgumentNotValid(
 *MethodArgumentNotValidException, HttpHeaders, HttpStatus, WebRequest)}。<br/>
 * 基本异常处理见{@linkplain GlobalExceptionHandler#exception(Exception)}。<br/>
 *
 * @author Jack
 * @date 2021/9/5
 * @since 1.0.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 处理逻辑见{@linkplain GlobalExceptionHandler#exception(Exception)}
     *
     * @param ex      异常对象
     * @param body    响应体对象
     * @param headers 请求头
     * @param status  请求状态码
     * @param request 请求对象
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        return exception(ex);
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
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.error("Processing method argument not valid", ex);
        final List<FieldError> allFieldErr = ex.getBindingResult().getAllErrors().stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(allFieldErr)) {
            return buildResponseEntity(ERROR);
        }
        return buildResponseEntity(handleFieldError(allFieldErr));
    }

    /**
     * 处理异常信息， 转为为{@linkplain ResponseEntity}对象， 以便统一异常处理的返回。<br/>
     * {@linkplain ResponseEntity#getBody()} 统一转化为 {@linkplain CommonResp}。<br/>
     * {@linkplain MethodArgumentNotValidException}异常需要特殊处理，
     * 详情见{@linkplain GlobalExceptionHandler#handleMethodArgumentNotValid(
     *MethodArgumentNotValidException, HttpHeaders, HttpStatus, WebRequest)}.<br/>
     * {@linkplain BusinessException} 业务异常可以直接转化成 {@linkplain CommonResp}。<br/>
     * 其他异常则返回 {@linkplain ExceptionEnum#ERROR}.
     *
     * @param ex 处理的异常， 包含业务异常
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        if (Objects.isNull(ex)) {
            return buildResponseEntity(ERROR);
        }

        log.error("Processing failed", ex);
        ExceptionCode errCode = ex instanceof BusinessException ? (BusinessException) ex : ERROR;
        return buildResponseEntity(errCode);
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
        final String errMsg = allFieldErr.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining());
        return new BusinessException(ERR_PARAM_VALID, errMsg);
    }

    /**
     * 将{@linkplain ExceptionCode 异常信息}转化成{@linkplain ResponseEntity Spring响应实体对象},
     * 其中{@linkplain ExceptionCode 异常信息}转化成 {@linkplain CommonResp}.<br/>
     *
     * @param errCode 异常信息
     * @return {@linkplain ResponseEntity Spring响应实体对象}
     */
    protected ResponseEntity<Object> buildResponseEntity(ExceptionCode errCode) {
        final String code = errCode.getCode();
        final String message = errCode.getMessage();
        log.error("Response Exception code:{}, message:{}", code, message);
        final CommonResp resp = new CommonResp();
        resp.setCode(code);
        resp.setMessage(message);
        return ResponseEntity.ok(resp);
    }
}
