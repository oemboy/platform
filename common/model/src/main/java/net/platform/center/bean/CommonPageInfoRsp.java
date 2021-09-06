package net.platform.center.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class CommonPageInfoRsp extends CommonResp {
    private PageInfo pageInfo = new PageInfo();
}
