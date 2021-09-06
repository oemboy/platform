
package net.platform.center.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.util.Preconditions;

import com.fasterxml.jackson.databind.JsonNode;
import net.platform.center.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author: bing zi
 * @Date: 2021/8/3 14:27
 */
@Slf4j
public class ResourceArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<ResourceFile> {
    private final BiFunction<Class<?>, String, InputStream> inputStreamBiFunction;

    private ResourceFile annotation;

    private String[] resources;

    private Charset charset;

    private Class<?> clazz;

    public ResourceArgumentsProvider() {
        this(Class::getResourceAsStream);
    }

    public ResourceArgumentsProvider(BiFunction<Class<?>, String, InputStream> inputStreamBiFunction) {
        this.inputStreamBiFunction = inputStreamBiFunction;
    }

    @Override
    public void accept(ResourceFile resourceFile) {
        this.annotation = resourceFile;
        this.resources = annotation.resources();
        this.clazz = annotation.type();
        try {
            this.charset = Charset.forName(annotation.encoding());
        } catch (UnsupportedCharsetException e) {
            throw new JUnitException("the charset is illegal!");
        }

    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Arrays.stream(resources)
            .map(resource -> openInputStream(extensionContext, resource))
            .flatMap(this::toStream);
    }

    private InputStream openInputStream(ExtensionContext extensionContext, String resource) {
        Preconditions.notBlank(resource, "[" + resource + "] must not be blank or null");
        Class<?> cla = extensionContext.getRequiredTestClass();
        return Preconditions.notNull(inputStreamBiFunction.apply(cla, resource),
            () -> "ClassPath resource [" + resource + "] is not exist");
    }

    private Stream<Arguments> toStream(InputStream inputStream) {
        String str = readResource(inputStream);
        if (String.class.equals(clazz)) {
            return Stream.of(Arguments.of(StringUtils.trim(str)));
        } else if (JsonNode.class.equals(clazz)) {
            return Stream.of(Arguments.of(JsonUtil.readJsonNode(str)));
        } else {
            return Stream.of(Arguments.of(JsonUtil.readValue(str, clazz)));
        }
    }

    private String readResource(InputStream inputStream) {
        StringBuilder builder = new StringBuilder(512);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            String line = reader.readLine();
            while (line != null) {
                builder.append(line).append(System.lineSeparator());
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.error("read resource file error!");
            throw new JUnitException("read resource file error!");
        }
        return builder.toString();
    }
}
