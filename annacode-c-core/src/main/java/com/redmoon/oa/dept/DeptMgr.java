package com.redmoon.oa.dept;


import cn.js.fan.util.ErrMsgException;
import cn.js.fan.web.Global;
import com.cloudweb.oa.utils.ConstUtil;
import com.redmoon.oa.pvg.Privilege;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class DeptMgr {
    String connname = "";
    Logger logger = Logger.getLogger(DeptMgr.class.getName());

    public DeptMgr() {
        connname = Global.getDefaultDB();
        if (connname.equals("")) {
            logger.info("Directory:默认数据库名不能为空");
        }
    }

    public DeptDb getDeptDb(String code) {
        DeptDb dd = new DeptDb();
        return dd.getDeptDb(code);
    }

    public Vector getChildren(String parentCode) throws ErrMsgException {
        DeptDb dd;
        if ("-1".equals(parentCode)) {
            Vector v = new Vector();
            v.addElement(getDeptDb(ConstUtil.DEPT_ROOT));
            return v;
        } else {
            dd = getDeptDb(parentCode);
        }
        return dd.getChildren();
    }

    /**
     * 获得 用户 可管理的每个部门下的用户
     * Author: LZM
     *
     * @param request
     * @return
     * @Description:
     */
    public ArrayList<String> getUserAdminDeptsUser(HttpServletRequest request) {
        ArrayList<String> list = new ArrayList<String>();
        DeptUserDb deptUserDb = new DeptUserDb();
        Privilege pvg = new Privilege();
        String userName = pvg.getUser();
        Vector<DeptDb> v = Privilege.getUserAdminDepts(userName);
        Iterator<DeptDb> ir = v.iterator();
        while (ir.hasNext()) {
            DeptDb dd = ir.next();
            // 部门下的所有用户
            Vector deptUsersVec = deptUserDb.list(dd.getCode());
            Iterator deptUsersIt = deptUsersVec.iterator();
            while (deptUsersIt.hasNext()) {
                DeptUserDb dUserDb = (DeptUserDb) deptUsersIt.next();
                list.add(dUserDb.getUserName());
            }
        }
        return list;
    }
}

