
package net.platform.center.util;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.MDC;

import lombok.extern.slf4j.Slf4j;
import net.platform.center.bean.DfxSqlBean;

/**
 * @Author: bing zi
 * @Date: 2021/6/4 15:22
 */
@Slf4j
@Intercepts(value = {@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class,
            BoundSql.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),})
public class SqlStatementInterceptor implements Interceptor {
    public static final long TIME_THRESHOLD = 1000L;

    public static final String SKIP_SQL_KEY = "skipSql";

    public static final String TIME_LIMIT_KEY = "dfxSqlTimeLimit";

    public static final String DEFAULT_SKIP_SQL = "recordDfxSql";

    private final Set<String> skipSqlSet = new HashSet<>(8);

    private long timeLimit = TIME_THRESHOLD;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue;

        final Object[] args = invocation.getArgs();

        MappedStatement mappedStatement = (MappedStatement) args[0];
        String sqlId = mappedStatement.getId();
        long start = System.currentTimeMillis();

        Object parameter = null;
        if (args.length > 1) {
            parameter = args[1];
        }
        DfxSqlBean dfxSqlBean = null;
        try {
            if (log.isDebugEnabled()) {
                dfxSqlBean = showDfxSql(mappedStatement, sqlId, parameter);
            }
            returnValue = invocation.proceed();
        } finally {
            setDfxSqlContext(mappedStatement, sqlId, start, parameter, dfxSqlBean);
        }
        return returnValue;
    }

    private void setDfxSqlContext(MappedStatement mappedStatement, String sqlId, long start, Object parameter,
        DfxSqlBean dfxSqlBean) {
        long time = System.currentTimeMillis() - start;
        if (time < timeLimit) {
            log.debug(costTime(sqlId, time));
        } else {
            log.warn(costTime(sqlId, time));
            if (dfxSqlBean == null && MDC.get(LogUtil.DFX_LOG_NAME) != null) {
                dfxSqlBean = showDfxSql(mappedStatement, sqlId, parameter);
            }
        }
        if (dfxSqlBean != null) {
            dfxSqlBean.setTimeSpan(time);
        }
    }

    private DfxSqlBean showDfxSql(MappedStatement mappedStatement, String sqlId, Object parameter) {
        DfxSqlBean dfxSqlBean = null;
        if (!sqlId.contains(DEFAULT_SKIP_SQL)) {
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            Configuration configuration = mappedStatement.getConfiguration();
            String sql = showSql(configuration, boundSql);
            log.debug("SqlStatementInterceptor PRINT sql[{}]:{}{}", sqlId, System.lineSeparator(), sql);
            if (skipSqlSet.stream().noneMatch(sqlId::contains)) {
                dfxSqlBean = new DfxSqlBean(sqlId, boundSql.getSql(), sql);
                ApplicationContextHolder.setDfxSql(dfxSqlBean);
            }
        }
        return dfxSqlBean;
    }

    public String costTime(String sqlId, long time) {
        return sqlId + ": 耗时【" + time + "】毫秒";
    }

    public String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format((Date) obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "NULL";
            }
        }
        return value;
    }

    public String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameter = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]]+", " ");
        try {
            if (CollectionUtils.isNotEmpty(parameterMappings) && parameter != null) {
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                if (typeHandlerRegistry.hasTypeHandler(parameter.getClass())) {
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameter)));
                } else {
                    MetaObject metaObject = configuration.newMetaObject(parameter);
                    for (ParameterMapping parameterMapping : parameterMappings) {
                        String name = parameterMapping.getProperty();
                        if (metaObject.hasGetter(name)) {
                            Object obj = metaObject.getValue(name);
                            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                        } else if (boundSql.hasAdditionalParameter(name)) {
                            Object obj = boundSql.getAdditionalParameter(name);
                            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                        } else {
                            sql = sql.replaceFirst("\\?", "缺失");
                        }
                    }
                }
            }
            return DfxFunction.formatStr(StringUtils.trim(sql));
        } catch (Exception e) {
            log.warn("fill sql parameter failure.");
            return sql;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        timeLimit = getTimeLimit(properties.getProperty(TIME_LIMIT_KEY));
        Optional.ofNullable(properties.getProperty(SKIP_SQL_KEY))
            .map(skips -> skips.split(","))
            .map(Arrays::stream)
            .ifPresent(this::toSqlSet);
        skipSqlSet.add(DEFAULT_SKIP_SQL);
    }

    private long getTimeLimit(String property) {
        if (StringUtils.isBlank(property)) {
            return TIME_THRESHOLD;
        }
        try {
            return Long.parseLong(property);
        } catch (Exception e) {
            log.warn("mybatis config error: {}", property);
            return TIME_THRESHOLD;
        }
    }

    private void toSqlSet(Stream<String> item) {
        item.filter(StringUtils::isNotBlank).forEach(skipSqlSet::add);
        log.debug("skipSqlSet: {}", skipSqlSet);
    }

}
