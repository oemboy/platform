
package net.platform.center.bean;

import static net.platform.center.util.LogUtil.TRANS_IDO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */

@Getter
@Setter
@ToString
public class CommonResp {
    private String logId = StringUtils.stripToEmpty(MDC.get(TRANS_IDO));

    private String code = "1";

    private String message = "success";

    public static void setErrRsp(CommonResp errRsp, String errMsg) {
        errRsp.setCode("0");
        errRsp.setMessage(errMsg);
    }
}
