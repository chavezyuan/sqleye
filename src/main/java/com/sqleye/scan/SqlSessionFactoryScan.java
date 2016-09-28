package com.sqleye.scan;

import com.sqleye.interceptor.SqlTimeInterceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by chavezyuan on 16/9/12.
 */
public class SqlSessionFactoryScan implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryScan.class);

    private static ApplicationContext applicationContext;

    private boolean on = true;

    private int threshold = 10;

    private static Map<String, Integer> selfEyeMap = new HashMap<>();

    public void init() {

        if(!on) {
            LOGGER.info("sql eye is off");
            return;
        }

        LOGGER.info("------ SqlSessionFactory Object scan start ------");

        LOGGER.info("sql time threshold: {}" ,threshold);

        Map<String, SqlSessionFactory> factoryMap = applicationContext.getBeansOfType(SqlSessionFactory.class);

        if(factoryMap.isEmpty()) {
            LOGGER.warn("[SqlSessionFactory Object not found]");
            return;
        }

        Set<Map.Entry<String, SqlSessionFactory>> factorySet = factoryMap.entrySet();

        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, SqlSessionFactory> factoryEntry : factorySet) {
            SqlSessionFactory factory = factoryEntry.getValue();
            Configuration config = factory.getConfiguration();
            if(config == null) {
                LOGGER.warn("[SqlSessionFactory Configuration is null][{}]", factoryEntry.getKey());
                continue;
            }
            config.addInterceptor(new SqlTimeInterceptor(threshold));
            sb.append("[").append(factoryEntry.getKey()).append("]");


            mapperScan(config);
        }

        LOGGER.info("[SqlSessionFactory Object scan result]{}", sb.toString());

        LOGGER.info("[SelfEye][{}]", SqlSessionFactoryScan.selfEyeMap.toString());

        LOGGER.info("------ SqlSessionFactory Object scan end ------");

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SqlSessionFactoryScan.applicationContext = applicationContext;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    private void mapperScan(Configuration config) {

        Collection<Class<?>> mappers = config.getMapperRegistry().getMappers();

        for(Class<?> mapper : mappers) {
            SelfEye selfEye = mapper.getAnnotation(SelfEye.class);
            Method[] methods = mapper.getDeclaredMethods();
            for(Method method : methods) {
                SelfEye methodSelfEye = method.getAnnotation(SelfEye.class);
                String key = mapper.getName() + "." + method.getName();
                if(methodSelfEye != null) {
                    SqlSessionFactoryScan.selfEyeMap.put(key, methodSelfEye.threthold());
                } else if(selfEye != null) {
                    SqlSessionFactoryScan.selfEyeMap.put(key, selfEye.threthold());
                }
            }
        }
    }

    public static Map<String, Integer> getSelfEyeMap() {
        return SqlSessionFactoryScan.selfEyeMap;
    }
}
