
package net.platform.kafka.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.event.KafkaEvent;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class KafkaEventListener implements ApplicationListener<KafkaEvent> {
    @Override
    public void onApplicationEvent(KafkaEvent event) {
        log.info(event.toString());
    }
}
