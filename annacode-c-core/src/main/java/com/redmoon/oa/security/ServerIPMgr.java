package com.redmoon.oa.security;

import cn.js.fan.util.ErrMsgException;
import cn.js.fan.util.ParamUtil;
import cn.js.fan.util.ResKeyException;
import cn.js.fan.util.StrUtil;
import com.cloudwebsoft.framework.db.JdbcTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ServerIPMgr {
    public ServerIPMgr() {
    }


    public boolean create(HttpServletRequest request) throws ErrMsgException, ResKeyException, IOException {
        ServerIPDb stDb = new ServerIPDb();
        String ip = ParamUtil.get(request, "ip");
        String description = ParamUtil.get(request, "description");
        return stDb.create(new JdbcTemplate(), new Object[]{ip, description});
    }

    public boolean save(HttpServletRequest request) throws ErrMsgException, ResKeyException, IOException {
        ServerIPDb stDb = new ServerIPDb();
        String ip = ParamUtil.get(request, "ip");
        String description = ParamUtil.get(request, "description");

        int id = ParamUtil.getInt(request, "id");
        stDb = (ServerIPDb) stDb.getQObjectDb(id);
        stDb.set("ip", ip);
        stDb.set("description", description);
        return stDb.save();
    }

    public boolean del(int id) throws Exception {
        ServerIPDb stDb = new ServerIPDb();
        boolean re = false;
        stDb = (ServerIPDb) stDb.getQObjectDb(id);
        re = stDb.del();
        return re;
    }

    /**
     * 全部删除
     *
     * @param request HttpServletRequest
     * @throws ErrMsgException
     */
    public void delBatch(HttpServletRequest request) throws ErrMsgException {
        String strids = ParamUtil.get(request, "ids");
        String[] ids = StrUtil.split(strids, ",");
        if (ids == null)
            return;
        int len = ids.length;
        for (int i = 0; i < len; i++) {
            try {
                del(StrUtil.toInt(ids[i]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
