
package net.platform.message.bean;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * 消息参数键值对
 *
 * @author jack
 * @since 0.0.3
 * @date 2022/07/07
 */
@Data
public class MessageParameter {

    /**
     * 参数名称
     */
    private String parameterName;

    /**
     * 参数值
     */
    private String parameterValue;

    public boolean nonEmptyParameter() {
        return StringUtils.isNotBlank(getParameterName()) && StringUtils.isNotBlank(getParameterValue());
    }
}
