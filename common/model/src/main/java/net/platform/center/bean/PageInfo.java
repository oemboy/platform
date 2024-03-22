
package net.platform.center.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author bing zi
 * @since 2021/6/4 15:22
 */

@Setter
public class PageInfo {
    @Getter
    private Integer beginRowNumber = 1;

    @Getter
    private Long totalRecords = 0L;

    private Integer currentPageNum = 0;

    private Integer pageSize = 10;

    @Getter
    private String sortOrder;

    @Getter
    private String sortField;

    @Getter
    private boolean isCount = true;

    public Integer getCurrentPageNum() {
        return currentPageNum == null ? Integer.valueOf(1) : currentPageNum;
    }

    public Integer getPageSize() {
        return pageSize == null ? Integer.valueOf(10) : pageSize;
    }

    public static Integer getPageSize(PageInfo pageInfo) {
        return pageInfo == null || pageInfo.getPageSize() == null || pageInfo.getPageSize() < 0 ? Integer.valueOf(10)
            : pageInfo.getPageSize();
    }

    public static Integer getCurrentPageNum(PageInfo pageInfo) {
        return pageInfo == null || pageInfo.getCurrentPageNum() == null || pageInfo.getCurrentPageNum() <= 0
            ? Integer.valueOf(1) : pageInfo.getCurrentPageNum();
    }
}
