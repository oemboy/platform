
package net.platform.center.util;

import static net.platform.center.exception.ExceptionEnum.ERR_COMMON_VALID;

import java.io.IOException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.exception.BusinessException;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    private JsonUtil() {

    }

    public static void configure(DeserializationFeature feature) {
        OBJECT_MAPPER.configure(feature, false);
    }

    public static void registerModule(Module module) {
        OBJECT_MAPPER.registerModule(module);
    }

    public static String toJsonString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static String toJsonPrettyString(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
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
        } catch (JsonProcessingException e) {
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
        } catch (JsonProcessingException e) {
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
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public static byte[] toJsonAsBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ERR_COMMON_VALID, e, e.getMessage());
        }
    }

    public static <T> T readValue(byte[] contentByte, Class<T> type) {
        if (ArrayUtils.isEmpty(contentByte)) {
            log.warn("content or type is empty");
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(contentByte, type);
        } catch (IOException e) {
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
}
