
package net.platform.message.service;

import java.util.List;

import net.platform.message.bean.MessageHistory;
import net.platform.message.bean.MessageTemplate;
import net.platform.message.bean.NotifyMessageInfo;
import net.platform.message.bean.PendingMessageInfo;
import net.platform.message.bean.ScanMessageCondition;
import net.platform.message.bean.MessageInfo;

/**
 * 消息通知处理服务
 *
 * @author Jack
 * @since 0.0.3
 */
public interface SystemMessageNotifyService {

    /**
     * 新增消息通知
     *
     * @param notifyMessage 新增消息通知对象
     * @return 新增消息ID
     */
    default Long addNotifyMessageInfo(NotifyMessageInfo notifyMessage) {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

    /**
     * 根据迁移主要信息将消息通知表的数据迁移到历史表
     * 
     * @param history 迁移主要信息
     * @return 迁移数量
     */
    default int messageMoveHistory(MessageHistory history) {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

    /**
     * 根据扫描消息信息条件, 扫描对应的待处理消息
     *
     * @param scanMessage 扫描消息信息条件
     * @return 待处理消息集合
     */
    default List<PendingMessageInfo> scanMessageInfo(ScanMessageCondition scanMessage) {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

    /**
     * 根据消息标识根据消息的信息
     *
     * @param updateMessage 需要更新的消息信息
     * @return 更新消息条数
     */
    default int updateMessageResult(MessageInfo updateMessage) {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

    /**
     * 根据模板号查询模板配置数据
     *
     * @param templateId 模板号
     * @return 模板配置数据
     */
    default MessageTemplate findTemplateById(String templateId) {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

}
