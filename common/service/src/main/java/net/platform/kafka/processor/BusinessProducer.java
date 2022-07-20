
package net.platform.kafka.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.concurrent.ListenableFuture;

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

    @Override
    public ListenableFuture<SendResult<Object, Object>> writeMessage(CommonMessage message, String topic) {
        log.debug("key: {}, value: {}", message.getKafkaKey(), message.getKafkaMessage());
        return sendWithPartition(message.getKafkaKey(), message.getKafkaMessage(), topic, -1);
    }

    @Override
    public ListenableFuture<SendResult<Object, Object>> writeMessageByPartition(CommonMessage message, String topic,
        int partition) {
        log.debug("key: {}, value: {}", message.getKafkaKey(), message.getKafkaMessage());
        return sendWithPartition(message.getKafkaKey(), message.getKafkaMessage(), topic, partition);
    }

    private ListenableFuture<SendResult<Object, Object>> sendWithPartition(String key, String payload, String topic,
        int partition) {
        final MessageBuilder<String> builder = MessageBuilder.withPayload(payload)
            .setHeader(KafkaHeaders.MESSAGE_KEY, key)
            .setHeader(KafkaHeaders.TOPIC, topic);

        String transIdo = LogUtil.getLogId();
        if (StringUtils.isNotEmpty(transIdo)) {
            builder.setHeader(LogUtil.TRANS_IDO, transIdo);
        }

        if (LogUtil.getDfxLogName() != null) {
            builder.setHeader(LogUtil.USE_DFX_LOG, LogUtil.getDfxLogName());
        }

        if (partition >= 0) {
            builder.setHeader(KafkaHeaders.PARTITION_ID, partition);
        }
        ListenableFuture<SendResult<Object, Object>> future = kafkaTemplate.send(builder.build());
        future.addCallback(this.successCallBack(), this.failureCallBack());
        return future;
    }

}
