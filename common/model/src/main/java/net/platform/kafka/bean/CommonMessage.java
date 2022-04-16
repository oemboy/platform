
package net.platform.kafka.bean;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
public interface CommonMessage {
    String getKafkaKey();

    String getKafkaMessage();

    String getMsgType();

    String getHisTopic();

    boolean isEmptyContent();

    void setMsgState(String state);
}
