
package net.platform.center.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.AntPathMatcher;

/**
 * @Author: jack
 * @Date: 2021/6/4 15:22
 */

public final class AntPathUtil {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private AntPathUtil() {
    }

    public static boolean match(Collection<String> antPaths, String path) {
        return CollectionUtils.emptyIfNull(antPaths).stream().anyMatch(antPath -> PATH_MATCHER.match(antPath, path));
    }

    public static boolean match(String antPath, String path) {
        return PATH_MATCHER.match(antPath, path);
    }

    public static String decodeUrl(String url) {
        return decodeUrl(url, StandardCharsets.UTF_8);
    }

    public static String decodeUrl(String url, Charset charset) {
        try {
            return URLDecoder.decode(url, charset.name());
        } catch (UnsupportedEncodingException e) {
            return "url decode error!";
        }
    }
}
