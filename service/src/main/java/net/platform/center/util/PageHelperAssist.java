
package net.platform.center.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.bean.CommonPageInfoReq;
import net.platform.center.bean.CommonPageInfoRsp;
import net.platform.center.bean.PageInfo;

/**
 * 分页辅助类
 * 
 * @author jack
 * @since 2021/6/4 15:22
 */

@Slf4j
public final class PageHelperAssist {
    private PageHelperAssist() {
    }

    public static <P extends CommonPageInfoRsp, Q extends CommonPageInfoReq, T> P page(Q req,
        Function<Q, List<T>> dataGet, Function<List<T>, P> rspFunc, boolean countFlag) {
        final PageInfo pageInfo = Optional.ofNullable(req).map(Q::getPageInfo).orElse(null);

        final List<T> data = getPageData(pageInfo, req, dataGet, countFlag);

        return getResult(rspFunc, data, pageInfo);
    }

    private static <P extends CommonPageInfoRsp, T> P getResult(Function<List<T>, P> rspFunc, List<T> data,
        PageInfo pageInfo) {
        if (data instanceof Page<T> pageData) {
            pageInfo.setTotalRecords(pageData.getTotal());
            pageInfo.setBeginRowNumber(Math.toIntExact(pageData.getStartRow()));
        }

        final P rsp = rspFunc.apply(data);

        if (Objects.nonNull(rsp)) {
            rsp.setPageInfo(pageInfo);
        }
        return rsp;
    }

    public static <P extends CommonPageInfoRsp, Q extends CommonPageInfoReq, T> P page(Q req,
        Function<Q, List<T>> dataGet, Function<List<T>, P> rspFunc) {
        return page(req, dataGet, rspFunc, req.getPageInfo().isCount());
    }

    public static <Q, T> List<T> getPageData(final PageInfo pageInfo, Q condition, Function<Q, List<T>> dataGet,
        boolean countFlag) {

        try (Page<T> page =
            PageMethod.startPage(PageInfo.getCurrentPageNum(pageInfo), PageInfo.getPageSize(pageInfo), countFlag)) {
            if (log.isDebugEnabled()) {
                log.debug("pages: {}", page);
            }

            return dataGet.apply(condition);
        }

    }

    public static <Q, T> List<T> getPageData(final PageInfo pageInfo, Q condition, Function<Q, List<T>> dataGet) {
        return getPageData(pageInfo, condition, dataGet, pageInfo.isCount());
    }

}
