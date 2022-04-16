
package net.platform.kafka.processor;

import java.nio.charset.Charset;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.Acknowledgment;

import lombok.extern.slf4j.Slf4j;
import net.platform.kafka.bean.CommonMessage;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
public abstract class AbstractConsumer<T extends CommonMessage> {
    public static final long SLEEP_TIME = 20;

    public void ackMsg(Acknowledgment ack, boolean flag) {
        if (flag) {
            ack.acknowledge();
        } else {
            ack.nack(0, SLEEP_TIME);
        }
    }

    public String getConsumerHeader(ConsumerRecord<?, ?> consumerRecord, String headerName) {
        final Header header = consumerRecord.headers().lastHeader(headerName);
        return (header == null || header.value() == null) ? "" : new String(header.value(), Charset.defaultCharset());
    }
}
