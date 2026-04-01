
package net.platform.message.bean;

import static net.platform.message.MessageConstant.DEFAULT_SCAN_SIZE;
import static net.platform.message.MessageConstant.MSG_PENDING;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扫描消息通知表条件对象
 * 
 * @author jack
 * @since 0.0.3
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanMessageCondition {
    /**
     * 业务类型, resetPassword重置密码
     */
    private String bizType;

    /**
     * 平台类型  短信, 邮件
     */
    private Integer platformType;

    /**
     * 优先级, 1低 2中 3高
     */
    private Integer priority;

    /**
     * 状态, 0待处理;1处理成功;2处理失败
     */
    @Builder.Default
    private Integer state = MSG_PENDING;

    /**
     * 扫描的数据量
     */
    @Builder.Default
    private Integer batchSize = DEFAULT_SCAN_SIZE;

    /**
     * 分片编号
     */
    private Integer shardingNum;

    /**
     * 分片总数
     */
    private Integer shardingCount;
}
