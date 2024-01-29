
package net.platform.message.bean;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 消息通知基本信息
 * 
 * @author jack
 * @since 0.0.3
 * @date 2022/07/07
 */

@Data
public class NotifyMessageInfo {

    private MessageInfo messageInfo;

    /**
     * 通知账号数组
     */
    private List<String> notifyAccount;

    /**
     * 参数键值对
     */
    private Map<String, Object> parameter;

    public Long getMessageId() {
        return this.messageInfo.getMessageId();
    }

    public void setMessageId(Long messageId) {
        this.messageInfo.setMessageId(messageId);
    }

    public Date getCreateDate() {
        return this.messageInfo.getCreateDate();
    }

    public void setCreateDate(Date createDate) {
        this.messageInfo.setCreateDate(createDate);
    }

    public Date getNotifyDate() {
        return this.messageInfo.getNotifyDate();
    }

    public void setNotifyDate(Date notifyDate) {
        this.messageInfo.setNotifyDate(notifyDate);
    }

    public Integer getState() {
        return this.messageInfo.getState();
    }

    public void setState(Integer state) {
        this.messageInfo.setState(state);
    }

    public Integer getRetryTimes() {
        return this.messageInfo.getRetryTimes();
    }

    public void setRetryTimes(Integer retryTimes) {
        this.messageInfo.setRetryTimes(retryTimes);
    }

    public String getTemplateId() {
        return this.messageInfo.getTemplateId();
    }

    public void setTemplateId(String templateId) {
        this.messageInfo.setTemplateId(templateId);
    }

    public Integer getPriority() {
        return this.messageInfo.getPriority();
    }

    public void setPriority(Integer priority) {
        this.messageInfo.setPriority(priority);
    }

    public String getBizCode() {
        return this.messageInfo.getBizCode();
    }

    public void setBizCode(String bizCode) {
        this.messageInfo.setBizCode(bizCode);
    }

    public static NotifyMessageInfoBuilder builder() {
        return new NotifyMessageInfoBuilder();
    }

    public static class NotifyMessageInfoBuilder {
        private Long messageId;

        private Date createDate;

        private Date notifyDate;

        private String bizCode;

        private Integer retryTimes;

        private Integer priority;

        private String templateId;

        private List<String> notifyAccount;

        private Map<String, Object> parameter;

        NotifyMessageInfoBuilder() {
        }

        public NotifyMessageInfoBuilder messageId(Long messageId) {
            this.messageId = messageId;
            return this;
        }

        public NotifyMessageInfoBuilder createDate(Date createDate) {
            this.createDate = createDate;
            return this;
        }

        public NotifyMessageInfoBuilder notifyDate(Date notifyDate) {
            this.notifyDate = notifyDate;
            return this;
        }

        public NotifyMessageInfoBuilder bizCode(String bizCode) {
            this.bizCode = bizCode;
            return this;
        }

        public NotifyMessageInfoBuilder retryTimes(Integer retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        public NotifyMessageInfoBuilder priority(Integer priority) {
            this.priority = priority;
            return this;
        }

        public NotifyMessageInfoBuilder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public NotifyMessageInfoBuilder notifyAccount(List<String> notifyAccount) {
            this.notifyAccount = notifyAccount;
            return this;
        }

        public NotifyMessageInfoBuilder parameter(Map<String, Object> parameter) {
            this.parameter = parameter;
            return this;
        }

        public NotifyMessageInfo build() {
            NotifyMessageInfo notifyMessageInfo = new NotifyMessageInfo();
            notifyMessageInfo.setMessageInfo(new MessageInfo());
            notifyMessageInfo.setMessageId(this.messageId);
            notifyMessageInfo.setCreateDate(this.createDate);
            notifyMessageInfo.setNotifyDate(this.notifyDate);
            notifyMessageInfo.setBizCode(this.bizCode);
            notifyMessageInfo.setNotifyDate(this.notifyDate);
            notifyMessageInfo.setRetryTimes(this.retryTimes);
            notifyMessageInfo.setTemplateId(this.templateId);
            notifyMessageInfo.setPriority(this.priority);
            notifyMessageInfo.setNotifyAccount(this.notifyAccount);
            notifyMessageInfo.setParameter(this.parameter);
            return notifyMessageInfo;
        }
    }
}
