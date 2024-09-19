
package net.platform.kafka.bean;

import java.util.Map;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */
public interface CommonMessage {
    /**
     * 获取 Partition
     *
     * @return msg
     */
    default int getPartition() {
        return -1;
    }

    /**
     * 获取 kafka 消息 key
     * 
     * @return key
     */
    String getKafkaKey();

    /**
     * 获取 kafka 消息
     * 
     * @return msg
     */
    String getKafkaMessage();

    /**
     * 获取 kafka 消息类型
     * 
     * @return msgType
     */
    String getMsgType();

    /**
     * 获取Topic
     *
     * @return msg
     */
    String getTopic();

    /**
     * 获取历史Topic
     * 
     * @return msg
     */
    String getHisTopic();

    /**
     * 消息是否为空
     * 
     * @return msg
     */
    boolean isEmptyContent();

    /**
     * 设置消息状态
     *
     * @param state 状态
     */
    void setMsgState(String state);

    /**
     * 获取消息上下文
     *
     * @return contextMap
     */
    Map<String, String> getContextMap();
}
