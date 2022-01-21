
package net.platform.center.util;

import static org.apache.commons.lang3.ArrayUtils.contains;

import java.util.Arrays;
import java.util.Deque;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class ApplicationContextHolder implements ApplicationContextAware {
    private static final ThreadLocal<RuntimeContext> RUNTIME_CONTEXT_HOLDER = new ThreadLocal<>();

    private static ApplicationContext context;

    public static RuntimeContext getRuntimeContext() {
        if (RUNTIME_CONTEXT_HOLDER.get() == null) {
            RUNTIME_CONTEXT_HOLDER.set(new RuntimeContext());
        }
        return RUNTIME_CONTEXT_HOLDER.get();
    }

    public static void setRuntimeContext(@NonNull RuntimeContext context) {
        RUNTIME_CONTEXT_HOLDER.set(context);
    }

    public static void clearRuntimeContext() {
        RUNTIME_CONTEXT_HOLDER.remove();
    }

    public static Object getContextMapValue(@NonNull String key) {
        return getRuntimeContext().getContextMap().get(key);
    }

    public static void setContextMapValue(@NonNull String key, Object value) {
        getRuntimeContext().getContextMap().put(key, value);
    }

    public static void removeContextMapEntry(@NonNull String key) {
        getRuntimeContext().getContextMap().remove(key);
    }

    public static String getRegion() {
        Deque<String> stack = getRuntimeContext().getRegions();
        return CollectionUtils.isEmpty(stack) ? null : stack.peek();
    }

    public static void setRegion(String region) {
        if (StringUtils.isBlank(region)) {
            return;
        }

        getRuntimeContext().getRegions().push(region);
    }

    public static void clearRegion() {
        Deque<String> stack = getRuntimeContext().getRegions();

        if (CollectionUtils.isNotEmpty(stack)) {
            stack.pop();
        }
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

    private static void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static <T> T getBean(String beaName, Class<T> type) {
        return context.getBean(beaName, type);
    }

    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }

    public static boolean matchProfiles(final String... profiles) {
        if (context == null) {
            return false;
        }

        if (ArrayUtils.isEmpty(profiles)) {
            return false;
        }

        final String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        final String[] defaultProfiles = context.getEnvironment().getDefaultProfiles();

        return Arrays.stream(profiles).anyMatch(profile -> contains(activeProfiles, profile))
            || Arrays.stream(profiles).anyMatch(profile -> contains(defaultProfiles, profile));
    }

}
