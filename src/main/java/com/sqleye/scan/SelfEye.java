package com.sqleye.scan;

import java.lang.annotation.*;

/**
 *
 * 自定义报警时间阈值
 *
 * Created by chavezyuan on 16/9/13.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SelfEye {

    int threthold() default 10;
}
