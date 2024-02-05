
package net.platform.center.bean;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */

@Data
@Builder
public class Dictionary implements Serializable {
    private String dictId;

    private String dictName;

    private String groupId;

    private String groupName;

    private int state;

    private String stateDate;

    private int sortOrder;

    private String memo;

}
