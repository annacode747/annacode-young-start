package cn.annacode.youngStart.org.framework.annotation;

import cn.annacode.youngStart.org.framework.enumerate.ScopeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    ScopeType value() default ScopeType.SINGLETON;
}
