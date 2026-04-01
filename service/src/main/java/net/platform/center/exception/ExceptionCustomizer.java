
package net.platform.center.exception;

/**
 * 支持接口自定义错误对象返回
 *
 * @author Jack
 */
public interface ExceptionCustomizer {

    /**
     * 通过当前请求对象判断是否支持自定义异常
     *
     * @param respComp 异常时返回信息和请求对象
     * @return 是否支持自定义异常, true表示支持, false表示不支持
     */
    boolean support(ResponseComponent respComp);

    /**
     * 根据异常描述信息自定义错误对象
     *
     * @param errCode 异常描述信息
     * @param respComp 异常时返回信息和请求对象
     * @return 异常返回对象
     */
    Object customize(ExceptionCode errCode, ResponseComponent respComp);
}
