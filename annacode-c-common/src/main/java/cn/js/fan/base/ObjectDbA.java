package cn.js.fan.base;

import cn.js.fan.db.ListResult;
import cn.js.fan.util.ErrMsgException;
import cn.js.fan.web.Global;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Vector;

public abstract class ObjectDbA implements Serializable {
    public String connname = "";
    public transient Logger logger = null;
    public String QUERY_LIST;
    public String QUERY_LOAD;
    public String QUERY_DEL;
    public String QUERY_SAVE;
    public String QUERY_ADD;

    public ObjectDbA() {
        init();
    }

    public void init() {
        logger = Logger.getLogger(this.getClass().getName());
        connname = Global.getDefaultDB();
        if (connname.equals(""))
            logger.info("默认数据库名不能为空");
        setQueryAdd();
        setQuerySave();
        setQueryDel();
        setQueryLoad();
        setQueryList();
    }

    abstract public void setQueryAdd();

    abstract public void setQuerySave();

    abstract public void setQueryDel();

    abstract public void setQueryLoad();

    abstract public void setQueryList();

    abstract public ListResult list(String listsql, int curPage, int pageSize) throws
            ErrMsgException;

    abstract public Vector list() throws SQLException;

    public boolean isLoaded() {
        return loaded;
    }

    abstract public void load();

    abstract public boolean save();

    abstract public boolean del();

    private boolean loaded = false;
}
