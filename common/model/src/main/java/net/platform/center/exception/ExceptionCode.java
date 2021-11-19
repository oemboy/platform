package net.platform.center.exception;

import java.text.MessageFormat;

/**
 * 抽象异常编码， 异常系统需包含 {@code code 业务异常编码} 和 {@code message 异常信息} 属性。<br/>
 * 异常返回示例：
 * <pre>
 *     {
 *       "code": "0",
 *       "message": "系统异常"
 *     }
 * </pre>
 *
 * @Author: Jack
 * @Date: 2021/9/5
 * @since 1.0.0
 */
public interface ExceptionCode {

    /**
     * 业务异常编码
     *
     * @return 业务异常编码
     */
    String getCode();

    /**
     * 异常信息
     *
     * @return 异常信息
     */
    String getMessage();

    /**
     * 异常信息
     *
     * @param args 格式化参数数组, 可变参数
     * @return 异常信息
     */
    default String getMessage(Object... args) {
        return MessageFormat.format(getMessage(), args);
    }
}
