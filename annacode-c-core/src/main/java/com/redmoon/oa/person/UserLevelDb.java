package com.redmoon.oa.person;

import cn.js.fan.base.ObjectDb;
import cn.js.fan.db.Conn;
import cn.js.fan.db.PrimaryKey;
import cn.js.fan.util.ErrMsgException;
import cn.js.fan.util.StrUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class UserLevelDb extends ObjectDb {

    public UserLevelDb() {
        super();
    }

    public UserLevelDb(int level) {
        this.level = level;
        init();
        load();
    }

    /**
     * 确定onlineTime所属的等级
     *
     * @param levelCompare int
     * @return UserLevelDb
     */
    public UserLevelDb getUserLevelDbByLevel(double onlineTime) {
        Vector v = getAllLevel();

        int len = v.size();

        // 未设用户等级
        if (len == 0)
            return new UserLevelDb();

        UserLevelDb myuld = (UserLevelDb) v.get(0);
        if (onlineTime < (myuld.getLevel()))
            return myuld;
        int i = 0;
        while (i < len) {
            myuld = (UserLevelDb) v.get(i);
            int lv = myuld.getLevel();
            if ((i + 1) < len) {
                UserLevelDb uld1 = (UserLevelDb) v.get(i + 1);
                int lv1 = uld1.getLevel();
                if (onlineTime >= lv && onlineTime < lv1) {
                    break;
                }
            } else {
                break;
            }
            i++;
        }
        return myuld;
    }

    public UserLevelDb getUserLevelDb(int level) {
        return (UserLevelDb) getObjectDb(new Integer(level));
    }

    public ObjectDb getObjectRaw(PrimaryKey pk) {
        return new UserLevelDb(pk.getIntValue());
    }

    public Vector getAllLevel() {
        UserLevelCache ulc = new UserLevelCache();
        return ulc.getAllLevel();
    }

    public void initDB() {
        this.tableName = "oa_user_level";
        primaryKey = new PrimaryKey("levelAmount", PrimaryKey.TYPE_INT);
        objectCache = new UserLevelCache(this);

        this.QUERY_DEL =
                "delete FROM " + tableName + " WHERE levelAmount=?";
        this.QUERY_CREATE =
                "INSERT into " + tableName + " (levelAmount, description, levelPicPath) VALUES (?,?,?)";
        this.QUERY_LOAD =
                "SELECT description, levelPicPath FROM " + tableName + " WHERE levelAmount=?";
        this.QUERY_SAVE =
                "UPDATE " + tableName + " SET levelAmount=?,description=?,levelPicPath=? WHERE levelAmount=?";
        this.QUERY_LIST = "select levelAmount from " + tableName + " order by levelAmount asc";
        isInitFromConfigDB = false;
    }

    public boolean save() throws ErrMsgException {
        // Based on the id in the object, get the message data from the database.
        Conn conn = new Conn(connname);
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(this.QUERY_SAVE);
            pstmt.setInt(1, newLevel);
            pstmt.setString(2, desc);
            pstmt.setString(3, levelPicPath);
            pstmt.setInt(4, level);
            if (conn.executePreUpdate() == 1) {
                UserLevelCache mc = new UserLevelCache(this);
                primaryKey.setValue(new Integer(level));
                mc.refreshSave(primaryKey);
                mc.refreshCreate();
                return true;
            } else
                return false;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ErrMsgException("保存时出错，请检查级别是否有重复！");
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }

            UserLevelCache uc = new UserLevelCache(this);
            primaryKey.setValue(new Integer(level));
            uc.refreshSave(primaryKey);
        }
    }

    public void load() {
        // Based on the id in the object, get the message data from the database.
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Conn conn = new Conn(connname);
        try {
            pstmt = conn.prepareStatement(this.QUERY_LOAD);
            pstmt.setInt(1, level);
            //url,title,image,userName,sort,kind
            rs = conn.executePreQuery();
            if (rs.next()) {
                this.desc = StrUtil.getNullString(rs.getString(1));
                this.levelPicPath = StrUtil.getNullString(rs.getString(2));
                primaryKey.setValue(new Integer(level));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }

    public boolean del() throws ErrMsgException {
        Conn conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = new Conn(connname);
            pstmt = conn.prepareStatement(this.QUERY_DEL);
            pstmt.setInt(1, level);
            if (conn.executePreUpdate() == 1) {
                UserLevelCache mc = new UserLevelCache(this);
                mc.refreshDel(primaryKey);
                return true;
            } else
                return false;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new ErrMsgException("删除出错！");
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
    }

    public boolean create() throws ErrMsgException {
        Conn conn = null;
        boolean re = false;
        PreparedStatement pstmt = null;
        try {
            conn = new Conn(connname);
            pstmt = conn.prepareStatement(this.QUERY_CREATE);
            pstmt.setInt(1, level);
            pstmt.setString(2, desc);
            pstmt.setString(3, levelPicPath);
            re = conn.executePreUpdate() == 1 ? true : false;
            if (re) {
                UserLevelCache mc = new UserLevelCache(this);
                mc.refreshCreate();
            }
        } catch (SQLException e) {
            logger.error("create:" + e.getMessage());
            throw new ErrMsgException("插入等级时出错！");
        } finally {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        }
        return re;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public String getDesc() {
        return desc;
    }

    public String getLevelPicPath() {
        return levelPicPath;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }

    public void setLevelPicPath(String levelPicPath) {
        this.levelPicPath = levelPicPath;
    }

    private int level;
    private String desc;
    private int newLevel;
    private String levelPicPath;

}
