
package net.platform.center.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

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
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue;
        final Object[] args = invocation.getArgs();

        MappedStatement mappedStatement = (MappedStatement) args[0];
        String sqlId = mappedStatement.getId();
        long start = System.currentTimeMillis();
        try {
            Object parameter = null;
            if (args.length > 1) {
                parameter = args[1];
            }

            if (log.isDebugEnabled()) {
                BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                Configuration configuration = mappedStatement.getConfiguration();
                String sql = showSql(configuration, boundSql);
                log.debug("SqlStatementInterceptor PRINT sql[{}]: {}", sqlId, sql);
            }
            returnValue = invocation.proceed();
        } finally {
            long time = System.currentTimeMillis() - start;
            if (time < 1000L) {
                log.debug(costTime(sqlId, time));

            } else {
                log.warn(costTime(sqlId, time));
            }
        }

        return returnValue;
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
        return StringUtils.trim(sql);
    }
}
