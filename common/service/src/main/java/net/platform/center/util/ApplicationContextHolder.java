
package net.platform.center.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.Stack;

import static org.apache.commons.lang3.ArrayUtils.contains;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class ApplicationContextHolder implements ApplicationContextAware {
    private static final ThreadLocal<RuntimeContext> RUNTIME_CONTEXT_HOLDER = new ThreadLocal<>();

    private static final ThreadLocal<Stack<String>> REGION = new ThreadLocal<>();

    private static ApplicationContext context;

    public static RuntimeContext getRuntimeContext() {
        if (RUNTIME_CONTEXT_HOLDER.get() == null) {
            RUNTIME_CONTEXT_HOLDER.set(new RuntimeContext());
        }
        return RUNTIME_CONTEXT_HOLDER.get();
    }

    public static void setRuntimeContext(RuntimeContext context) {
        RUNTIME_CONTEXT_HOLDER.set(context);
    }

    public static void clearRuntimeContext() {
        RUNTIME_CONTEXT_HOLDER.remove();
    }

    public static String getRegion() {
        Stack<String> stack = REGION.get();
        return CollectionUtils.isEmpty(stack) ? null : stack.peek();
    }

    public static void setRegion(String region) {
        if (StringUtils.isBlank(region)) {
            return;
        }
        if (REGION.get() == null) {
            REGION.set(new Stack<>());
        }
        REGION.get().push(region);
    }

    public static void clearRegion() {
        Stack<String> stack = REGION.get();
        if (stack == null) {
            return;
        }
        if (stack.size() > 0) {
            stack.pop();
        }
        if (stack.size() == 0) {
            REGION.remove();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
