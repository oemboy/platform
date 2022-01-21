
package net.platform.kafka.customize;

import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public class KafkaConsumerFactoryCustomize implements DefaultKafkaConsumerFactoryCustomizer {
    @Override
    public void customize(DefaultKafkaConsumerFactory<?, ?> consumerFactory) {

    }
}
