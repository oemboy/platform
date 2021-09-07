package net.platform.center.bean;

import lombok.Data;

@Data
public class PageInfo {
    private Integer beginRowNumber = 1;
    private Long totalRecords = 0L;

    private Integer currentPageNum = 0;
    private Integer pageSize = 10;

    private String sortOrder;
    private String sortField;
}
