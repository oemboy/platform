package net.platform.center.exception;

import net.platform.center.util.WebUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 扩展支持Url判断是否支持错误信息异常'
 *
 * @author Jack
 */
public abstract class AbstractAntPathExceptionCustomizer implements ExceptionCustomizer {

    @Override
    public boolean support(ResponseComponent respComp) {
        final String currUrl = WebUtil.currentUrlWithinApplication();
        return StringUtils.isNotBlank(currUrl) && supportUrl(currUrl, respComp);
    }

    /**
     * 通过当前请求URL判断是否支持自定义异常
     *
     * @param url      当前请求URL
     * @param respComp 异常时返回信息和请求对象
     * @return 是否支持自定义异常, true表示支持, false表示不支持
     */
    protected abstract boolean supportUrl(String url, ResponseComponent respComp);
}
