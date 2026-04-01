
package net.platform.center.test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.jupiter.params.support.ParameterDeclaration;
import org.junit.jupiter.params.support.ParameterDeclarations;
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
    public @NonNull Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters,
        ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        List<ParameterDeclaration> params = parameters.getAll(); // 获取方法参数列表
        int paramCount = params.size();
        int resourceCount = resources.length;
        // 场景 1：纵向迭代（经典模式）
        // 方法只有一个参数，但提供了 N 个资源 -> 跑 N 遍测试，每遍喂一个资源
        if (paramCount == 1) {
            return Arrays.stream(resources)
                .map(path -> Arguments.of(loadResource(testClass, path, params.getFirst().getParameterType())));
        }
        // 场景 2：横向扩展（进阶模式）
        // 方法有 N 个参数 -> 跑 1 遍测试，一次性喂 N 个资源
        if (paramCount > 1) {
            // 健壮性校验：资源文件必须够分
            if (resourceCount < paramCount) {
                throw new JUnitException(
                    String.format("测试方法需要 %d 个参数，但 @ResourceFile 只提供了 %d 个资源", paramCount, resourceCount));
            }
            Object[] argumentValues = new Object[paramCount];
            for (int i = 0; i < paramCount; i++) {
                // 绝杀：每个参数根据自己的类型（String/JsonNode/DTO）去加载对应的资源
                argumentValues[i] = loadResource(testClass, resources[i], params.get(i).getParameterType());
            }
            return Stream.of(Arguments.of(argumentValues));
        }
        return Stream.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NonNull Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        return Arrays.stream(resources).map(path -> Arguments.of(loadResource(testClass, path, null)));
    }

    @SneakyThrows
    private Object loadResource(Class<?> testClass, String path, Class<?> parameterType) {
        Preconditions.notBlank(path, "Resource path must not be blank");
        // 获取原始输入流
        InputStream is = testClass.getResourceAsStream(path);
        if (is == null) {
            throw new JUnitException("Resource not found: " + path);
        }

        Class<?> type = parameterType == null ? targetType : parameterType;
        // 1. 如果想要的是 InputStream，直接返回流（注意：调用方需要负责关闭流）
        if (InputStream.class.isAssignableFrom(type)) {
            return is;
        }
        // 2. 对于其他类型，我们需要读取内容后关闭流
        try (is) {
            // 如果想要字节数组（处理 PDF/二进制文件但不想用流时）
            if (byte[].class.equals(type)) {
                return is.readAllBytes();
            }
            // 如果是 String 或 JSON 相关转换
            String rawContent = new String(is.readAllBytes(), charset);

            return switch (type.getName()) {
                case "java.lang.String" -> StringUtils.trim(rawContent);
                case "com.fasterxml.jackson.databind.JsonNode", "tools.jackson.databind.JsonNode" ->
                    JsonUtil.readJsonNode(rawContent);
                default -> JsonUtil.readValue(rawContent, type);
            };
        } catch (Exception e) {
            log.error("Error processing resource file: {}", path, e);
            throw new JUnitException("Failed to process resource: " + path, e);
        }
    }
}
