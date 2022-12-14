package com.cloudwebsoft.framework.aop;

import com.cloudwebsoft.framework.aop.base.Advisor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Vector;

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
public class Binder implements InvocationHandler {
    Vector advisors;
    public Object proxyObj;

    public Binder() {
    }

    public Object bind(Object obj) {
        this.proxyObj = obj;
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(), this);
    }

    public void setAdvisors(Vector advisors) {
        this.advisors = advisors;
    }

    public void Before(Object proxy, Method method, Object[] args) throws
            Throwable {
        if (advisors == null)
            return;
        Iterator ir = advisors.iterator();
        while (ir.hasNext()) {
            Advisor advisor = (Advisor) ir.next();
            advisor.Before(proxy, method, args);
        }
    }

    public void After(Object proxy, Method method, Object[] args) throws
            Throwable {
        if (advisors == null)
            return;
        Iterator ir = advisors.iterator();
        while (ir.hasNext()) {
            Advisor advisor = (Advisor) ir.next();
            advisor.After(proxy, method, args);
        }
    }

    public void Throw(Object proxy, Method method, Object[] args, Exception e) throws
            Throwable {
        if (advisors == null)
            return;
        Iterator ir = advisors.iterator();
        while (ir.hasNext()) {
            Advisor advisor = (Advisor) ir.next();
            advisor.Throw(proxy, method, args, e);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws
            Throwable {
        Before(proxy, method, args);
        Object result = null;
        try {
            // ?????????????????????????????????????????????
            // System.out.println("??????log????????????" + method.getName());
            // ??????method.invoke????????????????????????ErrMsgException???????????????????????????invoke????????????????????????e.getMessage()???????????????null
            result = method.invoke(proxyObj, args); // ?????????
            // ??????????????????????????????????????????
        } catch (InvocationTargetException e) {
            // System.out.println("Binder.java invoke:" + e.getMessage() + " cause:" + e.getCause());
            e.printStackTrace();
            throw e.getTargetException();
            // Throw(proxy, method, args, e.getTargetException());
        }
        After(proxy, method, args);
        return result;
    }
}
