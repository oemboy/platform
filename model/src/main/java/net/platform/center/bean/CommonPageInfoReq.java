
package net.platform.center.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class CommonPageInfoReq extends RequestHeader {
    private PageInfo pageInfo = new PageInfo();
}
