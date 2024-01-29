
package net.platform.message.bean;

import lombok.Data;

import java.util.Date;

/**
 * 消息模板表ORM对象
 *
 * @author jack
 * @since 0.0.3
 * @date 2022/07/13
 */
@Data
public class MessageTemplate {

    /**
     * 模板号
     */
    private String templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 平台类型
     */
    private String platformType;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送人
     */
    private String sendFrom;

    /**
     * 扩展字段
     */
    private String extend;

    /**
     * 消息队列TOPIC
     */
    private String topicName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 状态时间
     */
    private Date stateDate;
}
