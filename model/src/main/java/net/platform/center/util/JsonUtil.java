
package net.platform.center.util;

import static net.platform.center.exception.ExceptionEnum.ERR_COMMON_VALID;

import java.io.InputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.exception.BusinessException;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Slf4j
public final class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
        // 配置特性
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    private JsonUtil() {
    }

    public static String toJsonString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JacksonException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static String toJsonPrettyString(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JacksonException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JacksonException e) {
            throw new BusinessException(ERR_COMMON_VALID, e, e.getMessage());
        }
    }

    public static <T> T readValue(String content, Class<T> type) {
        if (StringUtils.isEmpty(content) || type == null) {
            log.warn("content or type is empty");
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(content, type);
        } catch (JacksonException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) {
        if (StringUtils.isEmpty(content) || valueTypeRef == null) {
            log.warn("content or typeReference is empty");
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(content, valueTypeRef);
        } catch (JacksonException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static JsonNode readJsonNode(String content) {
        if (StringUtils.isEmpty(content)) {
            log.warn("content is empty");
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(content);
        } catch (JacksonException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static byte[] toJsonAsBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JacksonException e) {
            throw new BusinessException(ERR_COMMON_VALID, e, e.getMessage());
        }
    }

    public static <T> T readValue(byte[] contentByte, Class<T> type) {
        if (ArrayUtils.isEmpty(contentByte)) {
            log.warn("byte[] content or type is empty");
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(contentByte, type);
        } catch (JacksonException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueTypeRef);
    }

    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }

    public static ObjectNode createObjectNode() {
        return OBJECT_MAPPER.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return OBJECT_MAPPER.createArrayNode();
    }

    public static JsonNode readTree(InputStream in) {
        return OBJECT_MAPPER.readTree(in);
    }
}
