
package net.platform.message.bean;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 待处理的消息信息
 *
 * @author jack
 * @since 0.0.3
 * @date 2022/07/07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PendingMessageInfo extends MessageInfo {
    /**
     * 业务类型, 模板表字段
     */
    private String bizType;

    /**
     * 标题, 模板表字段
     */
    private String title;

    /**
     * 平台类型, 模板表字段
     */
    private String platformType;

    /**
     * 消息通知的账号列表
     */
    private List<String> accounts;

    /**
     * 消息模板的参数列表
     */
    private List<MessageParameter> parameters;
}
