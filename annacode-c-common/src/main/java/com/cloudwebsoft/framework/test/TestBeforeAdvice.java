package com.cloudwebsoft.framework.test;

import com.cloudwebsoft.framework.aop.advice.BeforeAdvice;

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
public class TestBeforeAdvice extends BeforeAdvice {
    public TestBeforeAdvice() {
    }

    /**
     * Before
     *
     * @param proxy  Object
     * @param method Method
     * @param args   Object[]
     * @throws Throwable
     * @todo Implement this com.cloudwebsoft.framework.aop.base.Advice method
     */
    public void Before(Object proxy, Method method, Object[] args) throws
            Throwable {
        System.out.println(this.getClass().getName() + " log here!");
    }
}
