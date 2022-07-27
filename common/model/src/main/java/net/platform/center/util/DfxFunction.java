
package net.platform.center.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author: bing zi
 * @Date: 2022/7/27
 */
public interface DfxFunction {
    /**
     * dfxSqlFunction
     */
    String SQL_RECORDER = "dfxSqlRecorder";

    /**
     * 记录dfx sql
     */
    void dfxFunction();

    /**
     * 格式化字符串(sql)
     * 
     * @param source 字符串
     * @return 格式化之后的字符串
     */
    static String formatStr(String source) {
        if (StringUtils.isEmpty(source)) {
            return source;
        }

        return Arrays.stream(StringUtils.replace(source, "\r\n", "\n").split("\n"))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
