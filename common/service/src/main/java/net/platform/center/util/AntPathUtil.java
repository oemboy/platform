
package net.platform.center.util;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.AntPathMatcher;

public abstract class AntPathUtil {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    public static boolean match(Collection<String> antPaths, String path) {
        return CollectionUtils.emptyIfNull(antPaths).stream().anyMatch(antPath -> PATH_MATCHER.match(antPath, path));
    }

    public static boolean match(String antPath, String path) {
        return PATH_MATCHER.match(antPath, path);
    }
}
