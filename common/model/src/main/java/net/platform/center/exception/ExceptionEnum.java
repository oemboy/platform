
package net.platform.center.exception;

import lombok.ToString;

/**
 * 异常码枚举, 实现{@linkplain ExceptionCode}.
 *
 * @Author: Jack
 * @Date: 2021/9/5
 * @since 1.0.0
 */
@ToString
public enum ExceptionEnum implements ExceptionCode {

    /**
     * 统一的处理成功返回码
     */
    OK("1", "Processing successful"),

    /**
     * 无法识别的异常返回码
     */
    ERROR("0", "Processing failed"),

    /**
     * 公共异常编码
     */
    ERR_PARAM_VALID("1000000", "Parameter valid error, message: {0}"),

    /**
     * 公共通用编码
     */
    ERR_COMMON_VALID("1000001", "error, message: {0}");

    private final String code;

    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
