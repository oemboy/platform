
package net.platform.message.bean;

import static net.platform.message.MessageConstant.MSG_PENDING;

import java.util.Date;

import lombok.Data;
import net.platform.center.exception.ExceptionEnum;
import net.platform.center.util.Assert;

@Data
public class MessageInfo {
    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 发送时间
     */
    private Date notifyDate;

    /**
     * 状态, 0待处理;1处理成功;2处理失败
     */
    private Integer state = MSG_PENDING;

    /**
     * 重试次数
     */
    private Integer retryTimes;

    /**
     * 模板号
     */
    private String templateId;

    /**
     * 优先级, 1低 2中 3高
     */
    private Integer priority;

    /**
     * 业务类型相关流水号
     */
    private String bizCode;

    public static MessageInfo ofPendingSysMessage(NotifyMessageInfo notifyMessage) {
        Assert.notNull(notifyMessage, ExceptionEnum.ERROR_BIZ, "空消息不支持入库");
        final MessageInfo msg = new MessageInfo();
        msg.setPriority(notifyMessage.getPriority());
        msg.setTemplateId(notifyMessage.getTemplateId());
        msg.setNotifyDate(notifyMessage.getNotifyDate());
        return msg;
    }
}
