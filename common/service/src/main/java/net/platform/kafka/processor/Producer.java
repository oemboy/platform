
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
public interface Producer<K, V> {
    org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Producer.class);

    /**
     * 发送消息
     *
     * @param message 发送消息
     * @param topic 主题
     * @return 发送结果
     */
    default ListenableFuture<SendResult<K, V>> writeMessage(CommonMessage message, String topic) {
        throw new UnsupportedOperationException("not support yet");
    }

    /**
     * 发送消息
     *
     * @param message 发送消息
     * @param topic 主题
     * @param partition 分区
     * @return 发送结果
     */
    default ListenableFuture<SendResult<K, V>> writeMessageByPartition(CommonMessage message, String topic,
        int partition) {
        throw new UnsupportedOperationException("not support yet");
    }

    /**
     * 失败回调
     *
     * @return 失败回调
     */
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

    /**
     * 解析失败
     * 
     * @param failedRecord 失败记录
     */
    default void parseFailedResult(ProducerRecord<K, V> failedRecord) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(failedRecord.toString());
        }
    }

    /**
     * 成功回调
     *
     * @return 成功回调
     */
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
