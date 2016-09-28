package com.sqleye.interceptor;


import com.sqleye.scan.SqlSessionFactoryScan;
import com.sqleye.util.StringUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by chavezyuan on 16/9/12.
 */

@Intercepts({
    @Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }),
    @Signature(method = "query", type = Executor.class, args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
    @Signature(method = "update", type = Executor.class, args = {MappedStatement.class, Object.class})})
public class SqlTimeInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlTimeInterceptor.class);

    private int threshold;

    public SqlTimeInterceptor() {}
    public SqlTimeInterceptor(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        long start = System.currentTimeMillis();

        Object rt = invocation.proceed();

        long time = System.currentTimeMillis() - start;

        MappedStatement ms = (MappedStatement)invocation.getArgs()[0];
        Object param = invocation.getArgs()[1];

        String sql = StringUtil.removeBreakingWhitespace(ms.getSqlSource().getBoundSql(param).getSql());
//        String params = JsonUtil.toJson(param);

        Integer msThreshold = SqlSessionFactoryScan.getSelfEyeMap().get(ms.getId());
        if(msThreshold == null) {
            msThreshold = this.threshold;
        }

        if(time > msThreshold.intValue()) {
            LOGGER.warn("[SQL Alert][threshold={}][actual={}][{}]", msThreshold.intValue(), time, sql);
        } else {
            LOGGER.debug("[SQL Info][threshold={}][actual={}][{}]", msThreshold.intValue(), time, sql);
        }

        return rt;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
