
package net.platform.center.util;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UrlPathHelper;

/**
 * 封装Servlet操作, 依赖Spring MVC 和 Servlet 环境
 *
 * @author Jack
 */
public final class WebUtil {

    private WebUtil() {
    }

    /**
     * 获取当前线程的请求对象
     *
     * @return 当前线程的请求对象
     */
    public static HttpServletRequest currentHttpServletRequest() {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attributes).getRequest();
        }
        return null;
    }

    /**
     * 获取当前请求Url, 忽略context-path
     *
     * @return 当前请求Url, 忽略context-path
     */
    public static String currentUrlWithinApplication() {
        return currentUrlWithinApplication(currentHttpServletRequest());
    }

    public static String currentUrlWithinApplication(final HttpServletRequest request) {
        return Objects.nonNull(request) ? UrlPathHelper.defaultInstance.getPathWithinApplication(request) : EMPTY;
    }

    public static void setRequestAttribute(String name, Object value) {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes)) {
            attributes.setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
        }
    }

    public static Object getRequestAttribute(String name) {
        final RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return Objects.nonNull(attributes) ? attributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST) : null;
    }
}
