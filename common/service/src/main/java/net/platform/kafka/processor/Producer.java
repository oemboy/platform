
package net.platform.kafka.processor;

import java.nio.charset.Charset;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.core.KafkaFailureCallback;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import net.platform.kafka.bean.CommonMessage;
import net.platform.center.util.LogUtil;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface Producer {
    default ListenableFuture<SendResult<Object, Object>> writeMessage(CommonMessage message, String topic) {
        throw new UnsupportedOperationException("not support yet");
    }

    default ListenableFuture<SendResult<Object, Object>> writeMessageByPartition(CommonMessage message, String topic,
        int partition) {
        throw new UnsupportedOperationException("not support yet");
    }

    default KafkaFailureCallback<Object, Object> failureCallBack() {
        return ex -> {
            try {
                final ProducerRecord<Object, Object> failedRecord = ex.getFailedProducerRecord();
                final String logId = getStringHeader(failedRecord, LogUtil.TRANS_IDO);
                LogUtil.setMdcInfo(logId);
            } finally {
                LogUtil.clearMdcInfo();
            }
        };
    }

    default SuccessCallback<SendResult<Object, Object>> successCallBack() {
        return result -> {
            try {
                final ProducerRecord<Object, Object> failedRecord = result.getProducerRecord();
                final String logId = getStringHeader(failedRecord, LogUtil.TRANS_IDO);
                String topic = result.getRecordMetadata().topic();
                int partition = result.getRecordMetadata().partition();
                long offset = result.getRecordMetadata().offset();
                LogUtil.setMdcInfo(logId);
            } finally {
                LogUtil.clearMdcInfo();
            }
        };
    }

    default String getStringHeader(ProducerRecord<Object, Object> record, String headerName) {
        final Header header = record.headers().lastHeader(headerName);
        return (header == null || header.value() == null) ? "" : new String(header.value(), Charset.defaultCharset());
    }
}
