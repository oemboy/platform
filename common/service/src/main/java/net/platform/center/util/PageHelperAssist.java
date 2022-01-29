
package net.platform.center.util;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.github.pagehelper.Page;
import com.github.pagehelper.page.PageMethod;

import net.platform.center.bean.CommonPageInfoReq;
import net.platform.center.bean.CommonPageInfoRsp;
import net.platform.center.bean.PageInfo;

/**
 * 分页辅助类
 */
public interface PageHelperAssist {

    static <P extends CommonPageInfoRsp, Q extends CommonPageInfoReq, T> P page(Q req, Function<Q, List<T>> dataGet,
        Function<List<T>, P> rspFunc, boolean countFlag) {
        final PageInfo pageInfo = Optional.ofNullable(req).map(Q::getPageInfo).orElse(null);
        if (Objects.isNull(pageInfo)) {
            throw new InvalidParameterException("CommonPageInfoReq::getPageInfo is null.");
        }
        PageMethod.startPage(PageInfo.getCurrentPageNum(pageInfo), PageInfo.getPageSize(pageInfo), countFlag);

        final List<T> data = Optional.ofNullable(dataGet.apply(req)).orElse(Collections.emptyList());
        if (data instanceof Page) {
            final Page<T> page = (Page<T>) data;
            pageInfo.setTotalRecords(page.getTotal());
            pageInfo.setBeginRowNumber(Math.toIntExact(page.getStartRow()));
        }

        final P rsp = rspFunc.apply(data);
        if (Objects.nonNull(rsp)) {
            rsp.setPageInfo(pageInfo);
        }
        return rsp;
    }

    static <P extends CommonPageInfoRsp, Q extends CommonPageInfoReq, T> P page(Q req, Function<Q, List<T>> dataGet,
        Function<List<T>, P> rspFunc) {
        return page(req, dataGet, rspFunc, true);
    }
}
