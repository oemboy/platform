
package net.platform.kafka.processor;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.core.KafkaFailureCallback;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import net.platform.center.util.LogUtil;
import net.platform.kafka.bean.CommonMessage;

import java.nio.charset.Charset;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface Producer<K, V> {
    org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Producer.class);

    default ListenableFuture<SendResult<K, V>> writeMessage(CommonMessage message, String topic) {
        throw new UnsupportedOperationException("not support yet");
    }

    default ListenableFuture<SendResult<K, V>> writeMessageByPartition(CommonMessage message, String topic,
        int partition) {
        throw new UnsupportedOperationException("not support yet");
    }

    default KafkaFailureCallback<K, V> failureCallBack() {
        return ex -> {
            try {
                final String logId = getProducerHeader(ex.getFailedProducerRecord(), LogUtil.TRANS_IDO);
                LogUtil.setMdcInfo(logId);
                parseFailedResult(ex.getFailedProducerRecord());
            } finally {
                LogUtil.clearMdcInfo();
            }
        };
    }

    default void parseFailedResult(ProducerRecord<K, V> failedRecord) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(failedRecord.toString());
        }
    }

    default SuccessCallback<SendResult<K, V>> successCallBack() {
        return result -> {
            try {
                if (result == null) {
                    LOG.warn("successCallBack result is null.");
                    return;
                }
                final String logId = getProducerHeader(result.getProducerRecord(), LogUtil.TRANS_IDO);
                LogUtil.setMdcInfo(logId);
                parseSuccessResult(result);
            } finally {
                LogUtil.clearMdcInfo();
            }
        };
    }

    default void parseSuccessResult(SendResult<K, V> result) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(result.toString());
        }
    }

    default String getProducerHeader(ProducerRecord<?, ?> producerRecord, String headerName) {
        final Header header = producerRecord.headers().lastHeader(headerName);
        return (header == null || header.value() == null) ? "" : new String(header.value(), Charset.defaultCharset());
    }

}
