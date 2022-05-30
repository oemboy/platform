
package net.platform.kafka.cfg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
@EnableScheduling
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "spring.kafka.cron", name = "enableCron", havingValue = "true")
public class KafkaCronConfig {
    @Resource
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Scheduled(cron = "${spring.kafka.cron.startCron}")
    public void startListener() {
        final Collection<MessageListenerContainer> listenerContainers =
            kafkaListenerEndpointRegistry.getListenerContainers();
        listenerContainers.stream()
            .filter(listenerContainer -> listenerContainer.getContainerProperties()
                .getClientId()
                .startsWith("cron-consumer"))
            .forEach(listenerContainer -> {
                log.info("listenerContainer clientId: {} {}", listenerContainer.getContainerProperties().getClientId(),
                    listenerContainer.getListenerId());

                if (!listenerContainer.isRunning()) {
                    log.info("start listener...");
                    listenerContainer.start();
                }
                log.info("resume listener...");
                listenerContainer.resume();
            });
    }

    @Scheduled(cron = "${spring.kafka.cron.stopCron}")
    public void stopListener() {
        // 0 0 12,22 * * ?
        final Collection<MessageListenerContainer> listenerContainers =
            kafkaListenerEndpointRegistry.getListenerContainers();
        listenerContainers.stream()
            .filter(listenerContainer -> listenerContainer.getContainerProperties()
                .getClientId()
                .startsWith("cron-consumer"))
            .forEach(listenerContainer -> {
                log.info("listenerContainer clientId: {} {}", listenerContainer.getContainerProperties().getClientId(),
                    listenerContainer.getListenerId());

                log.info("stop listener...");
                listenerContainer.pause();
            });
    }

    @PostConstruct
    public void init() {
        log.info("KafkaCronConfig init.");
    }
}
