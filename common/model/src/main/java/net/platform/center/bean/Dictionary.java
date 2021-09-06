package net.platform.center.bean;

import java.io.Serializable;
import lombok.Data;

@Data
public class Dictionary implements Serializable {
    private String dictId;
    private String dictName;
    private String groupId;

    private int state;
    private String stateDate;
    private int sortOrder;
    private String memo;


}
