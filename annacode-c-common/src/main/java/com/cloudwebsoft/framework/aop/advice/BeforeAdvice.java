package com.cloudwebsoft.framework.aop.advice;

import cn.js.fan.util.ErrMsgException;
import com.cloudwebsoft.framework.aop.base.Advice;

import java.lang.reflect.Method;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class BeforeAdvice implements Advice {
    public BeforeAdvice() {
    }

    abstract public void Before(Object proxy, Method method, Object[] args) throws
            Throwable;

    public void After(Object proxy, Method method, Object[] args) throws
            Throwable {
    }

    public void Throw(Object proxy, Method method, Object[] args, Exception e) throws
            Throwable {
        throw new ErrMsgException(e.getMessage());
    }
}
