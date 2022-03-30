
package net.platform.kafka.service;

import net.platform.kafka.bean.CommonMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.Acknowledgment;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public abstract class AbstractProcessor<T extends CommonMessage> {
    public static final long SLEEP_TIME = 20;

    @Value("${spring.kafka.consumer.group-id: testGroup}")
    public String groupId;

    public abstract T getMessage(ConsumerRecord<String, String> consumerRecord);

    public void ackMsg(Acknowledgment ack, boolean flag) {
        if (flag) {
            ack.acknowledge();
        } else {
            ack.nack(0, SLEEP_TIME);
        }
    }
}
