
package net.platform.center.bean;

import java.util.Date;

import lombok.Data;

/**
 * @author hjp
 * @date 2021/12/1 17:07
 */
@Data
public class SystemParam {

    /**
     * 参数表id
     */
    private String paramId;

    /**
     * 参数名称
     */
    private String paramName;

    /**
     * 参数值
     */
    private String paramValue;

    /**
     * 描述
     */
    private String remark;

    /**
     * 状态时间
     */
    private Date stateDate;

    /**
     * 状态 0 失效 1 生效
     */
    private Integer state;

    /**
     * 区域id
     */
    private Integer areaId = 999;
}
