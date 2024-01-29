
package net.platform.message.bean;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息通知迁移到历史表的主要信息
 * 
 * @author jack
 * @since 0.0.3
 * @date 2022/7/13
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class MessageHistory extends MessageInfo {

    /**
     * 内容
     */
    private String content;

    /**
     * 失败原因
     */
    private String failMessage;
}
