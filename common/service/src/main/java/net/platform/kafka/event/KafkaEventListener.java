
package net.platform.kafka.event;

import org.springframework.context.ApplicationListener;
import org.springframework.kafka.event.KafkaEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Slf4j
public class KafkaEventListener implements ApplicationListener<KafkaEvent> {
    @Override
    public void onApplicationEvent(KafkaEvent event) {
        if (log.isInfoEnabled()) {
            log.info(event.toString());
        }
    }
}
