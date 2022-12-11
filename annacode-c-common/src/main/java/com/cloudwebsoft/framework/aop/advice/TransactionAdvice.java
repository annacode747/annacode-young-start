package com.cloudwebsoft.framework.aop.advice;

import cn.js.fan.util.ErrMsgException;
import com.cloudwebsoft.framework.aop.base.Advice;
import com.cloudwebsoft.framework.db.JdbcTemplate;

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
public class TransactionAdvice implements Advice {
    public TransactionAdvice() {
    }

    public void Before(Object proxy, Method method, Object[] args) throws
            Throwable {
        int len = args.length;
        for (int i = 0; i < len; i++) {
            if (args[i] instanceof JdbcTemplate) {
                JdbcTemplate jt = (JdbcTemplate) args[i];
                jt.getConnection().beginTrans();
            }
        }
    }

    public void After(Object proxy, Method method, Object[] args) throws
            Throwable {
        int len = args.length;
        for (int i = 0; i < len; i++) {
            if (args[i] instanceof JdbcTemplate) {
                JdbcTemplate jt = (JdbcTemplate) args[i];
                jt.getConnection().commit();
            }
        }
    }

    public void Throw(Object proxy, Method method, Object[] args, Exception e) throws
            Throwable {
        throw new ErrMsgException(e.getMessage());
    }
}
