package com.cloudwebsoft.framework.base;

import cn.js.fan.cache.jcs.RMCache;
import cn.js.fan.db.KeyUnit;
import cn.js.fan.db.PrimaryKey;
import cn.js.fan.util.StrUtil;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DBConfig {
    RMCache rmCache;
    final String group = "cws_config_db";

    static Logger logger;
    public final String FILENAME = "configDB1.xml"; // 暂无使用XML文件初始化的ObjectDb

    public static Document doc = null;
    public static Element root = null;
    public static String xmlPath;
    public static boolean isInited = false;
    public static URL confURL;

    public DBConfig() {
        rmCache = RMCache.getInstance();

        logger = Logger.getLogger(this.getClass().getName());
        confURL = getClass().getResource("/" + FILENAME);
    }

    public static void init() {
        if (!isInited) {
            xmlPath = confURL.getPath();
            xmlPath = URLDecoder.decode(xmlPath);

            SAXBuilder sb = new SAXBuilder();
            try {
                FileInputStream fin = new FileInputStream(xmlPath);
                doc = sb.build(fin);
                root = doc.getRootElement();
                fin.close();
                isInited = true;
            } catch (org.jdom.JDOMException e) {
                logger.error(e.getMessage());
            } catch (java.io.IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Element getRootElement() {
        return root;
    }

    public void reload() {
        isInited = false;
        try {
            rmCache.invalidateGroup(group);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 取得表的配置信息
     *
     * @param objectName String
     * @return DBTable
     */
    public DBTable getDBTable(String objectName) {
        DBTable dt = null;
        try {
            dt = (DBTable) rmCache.getFromGroup(objectName, group);
        } catch (Exception e) {
            logger.error("getDBTable1:" + e.getMessage());
        }
        if (dt == null) {
            init();
            Element tables = root.getChild("tables");
            List list = tables.getChildren();
            if (list != null) {
                Iterator ir = list.iterator();
                while (ir.hasNext()) {
                    Element child = (Element) ir.next();
                    String objName = child.getAttributeValue("objName");
                    // logger.info("objName=" + objName + " objectName=" + objectName);
                    if (objName.equals(objectName)) {
                        String name = child.getAttributeValue("name");
                        String create = child.getChildText("create");
                        String load = child.getChildText("load");
                        String queryList = child.getChildText("list");
                        String save = child.getChildText("save");
                        String del = child.getChildText("del");
                        String objCache = child.getChildText("objectCache");

                        boolean objCachable = !StrUtil.getNullStr(child.getChildText("objCachable")).equals("false");
                        boolean listCachable = !StrUtil.getNullStr(child.getChildText("listCachable")).equals("false");

                        ObjectCache oc = null;
                        try {
                            oc = (ObjectCache) Class.forName(objCache).newInstance();
                        } catch (Exception e) {
                            logger.error("getDBTable:" + e.getMessage());
                        }

                        dt = new DBTable(name, objName);
                        dt.setQueryCreate(create);
                        dt.setQueryLoad(load);
                        dt.setQueryList(queryList);
                        dt.setQuerySave(save);
                        dt.setQueryDel(del);
                        dt.setObjectCache(oc);

                        dt.setObjCachable(objCachable);
                        dt.setListCachable(listCachable);

                        Element sqls = child.getChild("sqls");
                        if (sqls != null) {
                            List sqlList = sqls.getChildren("sql");
                            if (sqlList != null) {
                                Iterator sqlIr = sqlList.iterator();
                                while (sqlIr.hasNext()) {
                                    Element sql = (Element) sqlIr.next();
                                    String sqlName = sql.getAttributeValue("name");
                                    dt.sqls.put(sqlName, sql.getText());
                                }
                            }
                        }

                        Element pk = child.getChild("primaryKey");
                        String pkType = pk.getAttributeValue("type");
                        if (pkType.equals("String")) {
                            Element unit = pk.getChild("unit");
                            String pkName = unit.getChildText("name");
                            dt.setPrimaryKey(new PrimaryKey(pkName, PrimaryKey.TYPE_STRING));
                        } else if (pkType.equals("int")) {
                            Element unit = pk.getChild("unit");
                            String pkName = unit.getChildText("name");
                            dt.setPrimaryKey(new PrimaryKey(pkName, PrimaryKey.TYPE_INT));
                        } else if (pkType.equals("long")) {
                            Element unit = pk.getChild("unit");
                            String pkName = unit.getChildText("name");
                            dt.setPrimaryKey(new PrimaryKey(pkName,
                                    PrimaryKey.TYPE_LONG));
                        } else if (pkType.equals("compound")) {
                            List listpmk = pk.getChildren("unit");
                            Iterator irunit = listpmk.iterator();
                            HashMap key = new HashMap();
                            int orders = 0;
                            while (irunit.hasNext()) {
                                Element e = (Element) irunit.next();
                                String keyName = e.getChildTextTrim("name");
                                String keyType = e.getChildTextTrim("type");
                                switch (keyType) {
                                    case "String":
                                        key.put(keyName, new KeyUnit(PrimaryKey.TYPE_STRING, orders));
                                        break;
                                    case "int":
                                        key.put(keyName, new KeyUnit(PrimaryKey.TYPE_INT, orders));
                                        break;
                                    case "long":
                                        key.put(keyName, new KeyUnit(PrimaryKey.TYPE_LONG, orders));
                                        break;
                                    case "Date":
                                        key.put(keyName, new KeyUnit(PrimaryKey.TYPE_DATE, orders));
                                        break;
                                    default:
                                        logger.info("getDBTable: Parsing primary key of " + name + "，type=" + keyType + " is unknown!");
                                        break;
                                }
                                orders++;
                            }
                            dt.primaryKey = new PrimaryKey(key);
                        } else {
                            logger.info("getDBTable2: Parsing primary key of " + name + "，type=" + pkType + " is unknown!");
                        }

                        try {
                            rmCache.putInGroup(objName, group,
                                    dt);
                        } catch (Exception e) {
                            logger.error("getDBTable:" + e.getMessage());
                        }
                        return dt;
                    }
                }
            }
        } else {
            dt.renew();
        }
        return dt;
    }

    public void writemodify() {
        String indent = "    ";
        boolean newLines = true;
        Format format = Format.getPrettyFormat();
        format.setIndent(indent);
        format.setEncoding("utf-8");

        // XMLOutputter outp = new XMLOutputter(indent, newLines, "utf-8");
        XMLOutputter outp = new XMLOutputter(format);
        try {
            FileOutputStream fout = new FileOutputStream(xmlPath);
            outp.output(doc, fout);
            fout.close();
        } catch (java.io.IOException e) {
        }
    }

}


