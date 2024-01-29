
package net.platform.message.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息参数表ORM对象
 * 
 * @author jack
 * @since 0.0.3
 * @date 2022/07/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotifyMessageParameter extends MessageParameter {

    /**
     * 消息ID
     */
    private Long messageId;

    public static NotifyMessageParameter build(Long messageId, String name, String value) {
        final NotifyMessageParameter parameter = new NotifyMessageParameter();
        parameter.setMessageId(messageId);
        parameter.setParameterName(name);
        parameter.setParameterValue(value);
        return parameter;
    }
}
