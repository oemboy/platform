
package net.platform.kafka.customize;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */

@Slf4j
public class KafkaConsumerFactoryCustomize implements DefaultKafkaConsumerFactoryCustomizer {
    @Override
    public void customize(DefaultKafkaConsumerFactory<?, ?> consumerFactory) {
        log.info("KafkaConsumerFactory Customized.");
    }
}
