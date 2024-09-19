
package net.platform.kafka.processor;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.Acknowledgment;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.util.ApplicationContextHolder;
import net.platform.center.util.LogUtil;
import net.platform.kafka.bean.CommonMessage;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
@Slf4j
public abstract class AbstractConsumer<T extends CommonMessage> {
    public static final Duration SLEEP_TIME = Duration.ofMillis(20L);

    public void ackMsg(Acknowledgment ack, boolean flag) {
        if (flag) {
            ack.acknowledge();
        } else {
            ack.nack(0, SLEEP_TIME);
        }
        ApplicationContextHolder.clearRuntimeContext();
        LogUtil.clearMdcInfo();
    }

    public void setConsumerMdcInfo(Map<String, String> contextMap) {
        setConsumerMdcInfo(contextMap, LogUtil.INFO_TYPE);
    }

    public void setConsumerMdcInfo(Map<String, String> contextMap, String dfxLogLevel) {
        String transIdo = MapUtils.emptyIfNull(contextMap).get(LogUtil.TRANS_IDO);
        if (StringUtils.isNotEmpty(transIdo)) {
            LogUtil.setMdcInfo(transIdo);
        }

        String dfxLog = MapUtils.emptyIfNull(contextMap).get(LogUtil.USE_DFX_LOG);
        if (StringUtils.isNotEmpty(dfxLog)) {
            LogUtil.setMdcDfxInfo(dfxLog, dfxLogLevel);

        }
    }

    public String getConsumerHeader(ConsumerRecord<?, ?> consumerRecord, String headerName) {
        final Header header = consumerRecord.headers().lastHeader(headerName);
        return (header == null || header.value() == null) ? "" : new String(header.value(), Charset.defaultCharset());
    }

    /**
     * 获取消息实体
     *
     * @param consumerRecord kafka msg
     * @return 业务实体
     */
    public abstract T getMessage(ConsumerRecord<String, String> consumerRecord);
}
