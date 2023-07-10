
package net.platform.kafka.processor;

import java.nio.charset.Charset;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.SendResult;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface Producer<K, V> {
    org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Producer.class);

    /**
     * 解析成功
     * 
     * @param result 成功记录
     */
    default void parseSuccessResult(SendResult<K, V> result) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(result.toString());
        }
    }

    /**
     * 获取头信息
     * 
     * @param producerRecord 效力记录
     * @param headerName 头名称
     * @return 头信息
     */
    default String getProducerHeader(ProducerRecord<?, ?> producerRecord, String headerName) {
        final Header header = producerRecord.headers().lastHeader(headerName);
        return (header == null || header.value() == null) ? "" : new String(header.value(), Charset.defaultCharset());
    }

}
