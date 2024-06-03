
package net.platform.center.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
 * @author bing zi
 * @since 2021/6/4 15:22
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

    public static final String TIME_LIMIT_KEY = "dfxSqlTimeout";

    public static final String DEFAULT_SKIP_SQL = "recordDfxSql";

    private final Set<String> skipSqlSet = new HashSet<>(8);

    private long timeLimit = TIME_THRESHOLD;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        long start = System.currentTimeMillis();

        DfxSqlBean dfxSqlBean = null;
        try {
            if (log.isDebugEnabled()) {
                dfxSqlBean = showDfxSql(args);
            }
            return invocation.proceed();
        } finally {
            long span = System.currentTimeMillis() - start;
            if (span < timeLimit) {
                log.debug(costTime(span));
            } else {
                log.warn(costTime(span));
                setDfxSqlContext(args, span, dfxSqlBean);
            }
        }
    }

    private void setDfxSqlContext(Object[] args, long span, DfxSqlBean dfxSqlBean) {
        if (dfxSqlBean == null && MDC.get(LogUtil.DFX_LOG_NAME) != null) {
            dfxSqlBean = showDfxSql(args);
        }
        if (dfxSqlBean != null) {
            dfxSqlBean.setTimeSpan(span);
        }
    }

    private DfxSqlBean showDfxSql(Object[] args) {
        DfxSqlBean dfxSqlBean = null;
        Object parameter;
        BoundSql boundSql;
        try {
            // Executor method 'update' has two args
            // Executor method 'query' has six args
            MappedStatement mappedStatement = (MappedStatement) args[0];
            String sqlId = mappedStatement.getId();

            // 无需打印的sqlId
            if (skipSqlSet.stream().anyMatch(sqlId::contains)) {
                return null;
            }

            if (args.length == 6) {
                boundSql = (BoundSql) args[5];
                parameter = boundSql.getParameterObject();
            } else {
                parameter = args[1];
                boundSql = mappedStatement.getBoundSql(parameter);
            }

            Configuration configuration = mappedStatement.getConfiguration();
            String sql = showSql(configuration, parameter, boundSql);
            log.debug("SqlStatementInterceptor PRINT: [{}]:{}{}", sqlId, System.lineSeparator(), sql);

            dfxSqlBean = new DfxSqlBean(sqlId, boundSql.getSql(), sql);
            ApplicationContextHolder.setDfxSql(dfxSqlBean);
        } catch (Exception e) {
            log.warn("SqlStatementInterceptor build sql with parameters error: {}", e.getMessage());
        }
        return dfxSqlBean;
    }

    public String costTime(long time) {
        return "耗时【" + time + "】毫秒";
    }

    public String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date date) {
            value = "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date) + "'";
        } else if (obj instanceof LocalDateTime date) {
            value = "'" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'";
        } else if (obj instanceof Number num) {
            value = num.toString();
        } else {
            if (obj != null) {
                value = "'" + obj + "'";
            } else {
                value = "NULL";
            }
        }
        return value;
    }

    public String showSql(Configuration configuration, Object parameter, BoundSql boundSql) {
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
        try {
            return Long.parseLong(property);
        } catch (Exception e) {
            log.info("{} use default: {}", TIME_LIMIT_KEY, TIME_THRESHOLD);
            return TIME_THRESHOLD;
        }
    }

    private void toSqlSet(Stream<String> item) {
        item.filter(StringUtils::isNotBlank).map(StringUtils::trim).forEach(skipSqlSet::add);
        log.debug("skipSqlSet: {}", skipSqlSet);
    }
}
