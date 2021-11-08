
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

    public static Integer getPageSize(PageInfo pageInfo) {
        return pageInfo == null || pageInfo.getPageSize() == null || pageInfo.getPageSize() <= 0 ? Integer.valueOf(10)
            : pageInfo.getPageSize();
    }

    public static Integer getCurrentPageNum(PageInfo pageInfo) {
        return pageInfo == null || pageInfo.getCurrentPageNum() == null || pageInfo.getCurrentPageNum() <= 0
            ? Integer.valueOf(1) : pageInfo.getPageSize();
    }
}
