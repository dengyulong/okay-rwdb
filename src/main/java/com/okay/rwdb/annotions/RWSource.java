package com.okay.rwdb.annotions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.okay.rwdb.enums.RWType;

/**
 * 数据源注解 
 * @author dengyulong
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RWSource {
	RWType value() default RWType.MASTER;
}
