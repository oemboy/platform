
package net.platform.kafka.cfg;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Configuration(proxyBeanMethods = false)
@Slf4j
@Data
@EnableKafka
@ConditionalOnBean({KafkaProperties.class, ConcurrentKafkaListenerContainerFactoryConfigurer.class})
public class KafkaContainerConfig {

    @Bean("KafkaListenerContainerFactory")
    ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
        KafkaProperties kafkaProperties, ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
        ConsumerFactory<Object, Object> kafkaConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);

        Integer concurrency = kafkaProperties.getListener().getConcurrency();
        if (concurrency != null && concurrency > 0) {
            factory.setConcurrency(concurrency);
        }

        return factory;
    }
}
