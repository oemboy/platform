
package net.platform.center.cfg.redis;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * Redis 自定义自动配置（Spring Boot 4 兼容版）
 */
@AutoConfiguration
@AutoConfigureBefore(DataRedisAutoConfiguration.class)
@ConditionalOnClass({RedisTemplate.class, ObjectMapper.class})
public class RedisCustomAutoConfiguration {

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer(StandardCharsets.UTF_8);
    }

    /**
     * 推荐：使用 GenericJacksonJsonRedisSerializer + 安全的多态类型验证
     */
    @Bean
    public GenericJacksonJsonRedisSerializer jacksonJsonRedisSerializer(JsonMapper.Builder builder) {
        // 关键：安全的多态类型处理（替代 LaissezFaireSubTypeValidator）
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(Object.class)
            // .allowIfSubType("你的包名.") // 推荐只允许特定包，防止安全风险
            // .denyIfSubType("java.lang.") // 可显式拒绝某些危险类型
            .build();

        // 激活默认 typing（包含类信息，支持多态反序列化）
        builder.activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        return new GenericJacksonJsonRedisSerializer(builder.build());
    }

    @Bean("redisTemplate")
    public <V> RedisTemplate<String, V> redisTemplate(RedisConnectionFactory connectionFactory,
        StringRedisSerializer stringRedisSerializer, GenericJacksonJsonRedisSerializer jacksonJsonRedisSerializer) {

        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();

        // 设置连接工厂 + 序列化器
        setRedisTemplate(connectionFactory, redisTemplate, jacksonJsonRedisSerializer);

        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // 初始化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private <V> void setRedisTemplate(RedisConnectionFactory connectionFactory, RedisTemplate<String, V> redisTemplate,
        GenericJacksonJsonRedisSerializer jacksonJsonRedisSerializer) {
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(jacksonJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jacksonJsonRedisSerializer);
        redisTemplate.setDefaultSerializer(jacksonJsonRedisSerializer);
    }
}