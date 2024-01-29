
package net.platform.center.bean;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 包含业务数据的通用返回对象
 * 
 * @author Jack
 * @since 1.0.1
 */
@Data
@Builder(builderMethodName = "response")
@EqualsAndHashCode(callSuper = true)
public class CommonDataResponse<T> extends CommonResp {
    private T body;
}
