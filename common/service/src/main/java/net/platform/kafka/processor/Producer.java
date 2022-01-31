
package net.platform.kafka.processor;

import java.nio.charset.Charset;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.core.KafkaFailureCallback;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import net.platform.center.util.LogUtil;
import net.platform.kafka.bean.CommonMessage;

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
                parseFailedResult(failedRecord);
            } finally {
                LogUtil.clearMdcInfo();
            }
        };
    }

    default void parseFailedResult(ProducerRecord<Object, Object> failedRecord) {
    }

    default SuccessCallback<SendResult<Object, Object>> successCallBack() {
        return result -> {
            try {
                final ProducerRecord<Object, Object> successRecord = result.getProducerRecord();
                final String logId = getStringHeader(successRecord, LogUtil.TRANS_IDO);
                LogUtil.setMdcInfo(logId);
                parseSuccessResult(result);
            } finally {
                LogUtil.clearMdcInfo();
            }
        };
    }

    default void parseSuccessResult(SendResult<Object, Object> result) {
    }

    default String getStringHeader(ProducerRecord<Object, Object> record, String headerName) {
        final Header header = record.headers().lastHeader(headerName);
        return (header == null || header.value() == null) ? "" : new String(header.value(), Charset.defaultCharset());
    }
}
