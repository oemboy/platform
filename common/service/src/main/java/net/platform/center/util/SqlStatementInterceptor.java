
package net.platform.center.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private final Set<String> skipSqlSet = new HashSet<>(8);

    private long timeLimit = TIME_THRESHOLD;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        long start = System.currentTimeMillis();

        DfxSqlBean dfxSqlBean = null;
        boolean showSqlFlag = false;
        try {
            if (log.isDebugEnabled()) {
                showSqlFlag = true;
                dfxSqlBean = showDfxSql(args);
            }
            return invocation.proceed();
        } finally {
            long span = System.currentTimeMillis() - start;
            // sql消耗时间较少时或者debug未打开时, 不记录sql
            if (span < timeLimit) {
                log.debug("sql cost time: {}", span);
            } else {
                // debug未打开时, 但是sql时间超长时, 依然记录sql
                log.warn("sql cost too much time: {}", span);
                setDfxSqlContext(args, span, dfxSqlBean, showSqlFlag);
            }
        }
    }

    private void setDfxSqlContext(Object[] args, long span, DfxSqlBean dfxSqlBean, boolean showSqlFlag) {
        boolean showSqlOnceMore = dfxSqlBean == null && !showSqlFlag;
        DfxSqlBean contextSqlBean = showSqlOnceMore ? showDfxSql(args) : dfxSqlBean;
        if (contextSqlBean != null) {
            contextSqlBean.setTimeSpan(span);
            ApplicationContextHolder.setDfxSql(dfxSqlBean);
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

            // 无需打印的 sqlId
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
            if (log.isDebugEnabled()) {
                log.debug("SqlStatementInterceptor PRINT: [{}]:{}{}", sqlId, System.lineSeparator(), sql);
            }

            dfxSqlBean = new DfxSqlBean(sqlId, boundSql.getSql(), sql);
        } catch (Exception e) {
            log.warn("SqlStatementInterceptor build sql with parameters error: {}", e.getMessage());
        }
        return dfxSqlBean;
    }

    public String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date date) {
            Instant instant = Instant.ofEpochMilli(date.getTime());
            value = "'" + DATE_TIME_FORMATTER.format(instant) + "'";
        } else if (obj instanceof LocalDateTime date) {
            value = "'" + date.format(DATE_TIME_FORMATTER) + "'";
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

        String sql = boundSql.getSql().replaceAll("\\s+", " ");
        int totalParams = 0;

        try {
            if (CollectionUtils.isNotEmpty(parameterMappings) && parameter != null) {
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                if (typeHandlerRegistry.hasTypeHandler(parameter.getClass())) {
                    // 统计单参数
                    totalParams++;
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameter)));
                } else {
                    StringBuilder sb = new StringBuilder();
                    int lastPos = 0;
                    int mappingIndex = 0;
                    MetaObject metaObject = configuration.newMetaObject(parameter);

                    while (true) {
                        int currentPos = sql.indexOf("?", lastPos);
                        if (currentPos == -1 || mappingIndex >= parameterMappings.size()) {
                            break;
                        }
                        sb.append(sql, lastPos, currentPos);

                        ParameterMapping parameterMapping = parameterMappings.get(mappingIndex++);
                        String name = parameterMapping.getProperty();
                        Object obj;
                        if (metaObject.hasGetter(name)) {
                            obj = metaObject.getValue(name);
                        } else if (boundSql.hasAdditionalParameter(name)) {
                            obj = boundSql.getAdditionalParameter(name);
                        } else {
                            sb.append("缺失");
                            lastPos = currentPos + 1;
                            continue;
                        }

                        if (obj != null) {
                            totalParams++;
                        }

                        sb.append(getParameterValue(obj));
                        lastPos = currentPos + 1;
                    }
                    sb.append(sql.substring(lastPos));
                    sql = sb.toString();
                }
            }

            // 参数过多打一条 WARN
            if (totalParams > 500) {
                log.warn("【SQL审计】检测到潜在错误查询! , 参数总数: {}", totalParams);
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
