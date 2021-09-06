
package net.platform.center.cfg.redis;

import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Configuration(proxyBeanMethods = false)
public class RedisConfig {

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer(StandardCharsets.UTF_8);
    }

    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean("redisTemplate")
    public <V> RedisTemplate<String, V> redisTemplate(RedisConnectionFactory connectionFactory,
        StringRedisSerializer stringRedisSerializer, GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
        setRedisTemplate(connectionFactory, redisTemplate, jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(stringRedisSerializer);
        return redisTemplate;
    }

    private <V> void setRedisTemplate(RedisConnectionFactory connectionFactory, RedisTemplate<String, V> redisTemplate,
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
    }
}
