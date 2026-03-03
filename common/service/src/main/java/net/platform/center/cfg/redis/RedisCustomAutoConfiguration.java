
package net.platform.center.cfg.redis;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */

@AutoConfiguration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@ConditionalOnClass({RedisTemplate.class, ObjectMapper.class})
public class RedisCustomAutoConfiguration {

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer(StandardCharsets.UTF_8);
    }

    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer(Jackson2ObjectMapperBuilder builder) {
        // 构建一个遵循下游项目配置的 ObjectMapper
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();

        // 必须进行 Redis 特有的定制：在 JSON 中包含类信息
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean("redisTemplate")
    public <V> RedisTemplate<String, V> redisTemplate(RedisConnectionFactory connectionFactory,
        StringRedisSerializer stringRedisSerializer, GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        setRedisTemplate(connectionFactory, redisTemplate, jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 初始化检查
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private <V> void setRedisTemplate(RedisConnectionFactory connectionFactory, RedisTemplate<String, V> redisTemplate,
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
    }
}
