
package net.platform.center.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 包含分页业务数据的通用返回对象
 *
 * @author Jack
 * @since 1.0.1
 */
@Data
@Builder(builderMethodName = "response")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommonPageResponse<T> extends CommonPageInfoRsp {
    private T body;
}
