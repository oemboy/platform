
package net.platform.center.test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.Preconditions;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.platform.center.util.JsonUtil;

/**
 * @author bing zi
 * @since 2021/8/3 14:27
 */
@Slf4j
public class ResourceArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<ResourceFile> {
    private String[] resources;

    private Charset charset;

    private Class<?> targetType;

    @Override
    public void accept(ResourceFile resourceFile) {
        this.resources = resourceFile.resources();
        this.targetType = resourceFile.type();
        this.charset = Charset.forName(resourceFile.encoding());
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        return Arrays.stream(resources).map(path -> Arguments.of(loadResource(testClass, path)));
    }

    @SneakyThrows
    private Object loadResource(Class<?> testClass, String path) {
        Preconditions.notBlank(path, "Resource path must not be blank");
        // 获取原始输入流
        InputStream is = testClass.getResourceAsStream(path);
        if (is == null) {
            throw new JUnitException("Resource not found: " + path);
        }

        // 1. 如果想要的是 InputStream，直接返回流（注意：调用方需要负责关闭流）
        if (InputStream.class.isAssignableFrom(targetType)) {
            return is;
        }

        // 2. 对于其他类型，我们需要读取内容后关闭流
        try (is) {
            // 如果想要字节数组（处理 PDF/二进制文件但不想用流时）
            if (byte[].class.equals(targetType)) {
                return is.readAllBytes();
            }

            // 如果是 String 或 JSON 相关转换
            String rawContent = new String(is.readAllBytes(), charset);

            return switch (targetType.getName()) {
                case "java.lang.String" -> StringUtils.trim(rawContent);
                case "com.fasterxml.jackson.databind.JsonNode" -> JsonUtil.readJsonNode(rawContent);
                default -> JsonUtil.readValue(rawContent, targetType);
            };
        } catch (Exception e) {
            log.error("Error processing resource file: {}", path, e);
            throw new JUnitException("Failed to process resource: " + path, e);
        }
    }
}
