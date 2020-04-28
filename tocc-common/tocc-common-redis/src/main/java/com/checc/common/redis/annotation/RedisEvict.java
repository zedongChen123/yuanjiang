package com.checc.common.redis.annotation;

import java.lang.annotation.*;

/**
 *redis删除注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisEvict
{
    String key();

    String fieldKey();
}