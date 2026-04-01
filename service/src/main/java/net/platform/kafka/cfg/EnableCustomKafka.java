
package net.platform.kafka.cfg;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({KafkaCustomConfiguration.class})
public @interface EnableCustomKafka {
}
