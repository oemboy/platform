
package net.platform.kafka.processor;

import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.util.LogUtil;
import net.platform.kafka.bean.CommonMessage;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public class BusinessProducer implements Producer<Object, Object> {

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public CompletableFuture<SendResult<Object, Object>> writeMessage(CommonMessage message, String topic) {
        setContextMap(message);
        if (log.isDebugEnabled()) {
            log.debug("key: {}, value: {}", message.getKafkaKey(), message.getKafkaMessage());
        }
        return sendWithPartition(message.getKafkaKey(), message.getKafkaMessage(), topic, -1);
    }

    private static void setContextMap(CommonMessage message) {
        String transIdo = LogUtil.getLogId();
        if (StringUtils.isNotEmpty(transIdo)) {
            message.getContextMap().put(LogUtil.TRANS_IDO, transIdo);
        }

        if (LogUtil.getDfxLogName() != null) {
            message.getContextMap().put(LogUtil.USE_DFX_LOG, LogUtil.getDfxLogName());
        }
    }

    public CompletableFuture<SendResult<Object, Object>> writeMessageByPartition(CommonMessage message, String topic,
        int partition) {
        setContextMap(message);
        if (log.isDebugEnabled()) {
            log.debug("key: {}, value: {}", message.getKafkaKey(), message.getKafkaMessage());
        }
        return sendWithPartition(message.getKafkaKey(), message.getKafkaMessage(), topic, partition);
    }

    private CompletableFuture<SendResult<Object, Object>> sendWithPartition(String key, String payload, String topic,
        int partition) {
        final MessageBuilder<String> builder =
            MessageBuilder.withPayload(payload).setHeader(KafkaHeaders.KEY, key).setHeader(KafkaHeaders.TOPIC, topic);

        if (partition >= 0) {
            builder.setHeader(KafkaHeaders.PARTITION, partition);
        }

        return kafkaTemplate.send(builder.build());
    }

}
