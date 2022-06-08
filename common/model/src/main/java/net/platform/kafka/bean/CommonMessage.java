
package net.platform.kafka.bean;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface CommonMessage {
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
}
