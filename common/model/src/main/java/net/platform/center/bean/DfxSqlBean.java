
package net.platform.center.bean;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.util.DfxFunction;
import net.platform.center.util.DigestUtil;

/**
 * @author bing zi
 * @since 2022/7/27
 */

@Slf4j
@Data
public class DfxSqlBean {
    private String sqlId;

    private String sqlIndex;

    private String sql;

    private long timeSpan;

    public DfxSqlBean(String sqlId, String boundSql, String sql) {
        this.sqlId = sqlId;
        this.sqlIndex = getSha256(sqlId, boundSql);
        this.sql = DfxFunction.formatStr(sql);
    }

    private String getSha256(String sqlId, String boundSql) {
        try {
            return DigestUtil.getSha256(format(boundSql));
        } catch (NoSuchAlgorithmException e) {
            log.warn("Sha256 is not exist.");
            return sqlId;
        }
    }

    private String format(String boundSql) {
        if (StringUtils.isBlank(boundSql)) {
            return boundSql;
        }
        return DfxFunction.formatStr(boundSql.replaceAll("[\\s]]+", " ")).toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DfxSqlBean that = (DfxSqlBean) o;
        return sqlIndex.equals(that.sqlIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sqlIndex);
    }
}
