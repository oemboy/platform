package net.platform.center.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author Jack
 */
public class BusinessException extends RuntimeException implements ExceptionCode {
    @Getter
    private final String code;
    @Getter
    private final String message;

    public BusinessException(ExceptionCode code, Object... args) {
        super(code.toString());
        this.code = code.getCode();
        this.message = code.getMessage(args);
    }

    public BusinessException(ExceptionCode code, Throwable cause, Object... args) {
        super(code.toString(), cause);
        this.code = code.getCode();
        this.message = code.getMessage(args);
    }

    public BusinessException(ExceptionCode code, Throwable cause,
                             boolean enableSuppression,
                             boolean writableStackTrace,
                             Object... args) {
        super(code.toString(), cause, enableSuppression, writableStackTrace);
        this.code = code.getCode();
        this.message = code.getMessage(args);
    }
}
