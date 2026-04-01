
package net.platform.message.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息账号表ORM对象
 * 
 * @author jack
 * @since 0.0.3
 * @date 2022/07/07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageAccount {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 通知账号
     */
    private String notifyAccount;
}
