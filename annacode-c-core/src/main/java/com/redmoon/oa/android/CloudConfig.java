package com.redmoon.oa.android;

import cn.js.fan.util.ErrMsgException;
import cn.js.fan.util.StrUtil;
import cn.js.fan.util.XMLProperties;
import com.cloudwebsoft.framework.util.LogUtil;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

public class CloudConfig {
    // public: constructor to load driver and connect db
    private XMLProperties properties;
    private final String CONFIG_FILENAME = "config_cloud.xml";

    private String cfgpath;

    Logger logger;

    Document doc = null;
    Element root = null;

    public static CloudConfig cfg = null;
    private static Object initLock = new Object();

    public CloudConfig() {
    }

    public void init() {
        logger = Logger.getLogger(CloudConfig.class.getName());
        URL cfgURL = getClass().getResource("/" + CONFIG_FILENAME);
        cfgpath = cfgURL.getFile();
        cfgpath = URLDecoder.decode(cfgpath);
        properties = new XMLProperties(cfgpath);

        SAXBuilder sb = new SAXBuilder();
        try {
            FileInputStream fin = new FileInputStream(cfgpath);
            doc = sb.build(fin);
            root = doc.getRootElement();
            fin.close();
        } catch (JDOMException e) {
            LogUtil.getLog(getClass()).error("init:" + e.getMessage());
        } catch (IOException e) {
            LogUtil.getLog(getClass()).error("init2:" + e.getMessage());
        }
    }

    public Element getRoot() {
        return root;
    }

    public static CloudConfig getInstance() {
        if (cfg == null) {
            synchronized (initLock) {
                cfg = new CloudConfig();
                cfg.init();
            }
        }
        return cfg;
    }

    public static void reload() {
        cfg = null;
    }

    public String getProperty(String name) {
        return StrUtil.getNullStr(properties.getProperty(name));
    }

    public int getIntProperty(String name) {
        String p = getProperty(name);
        if (StrUtil.isNumeric(p)) {
            return Integer.parseInt(p);
        } else if ("-1".equals(p)) {
            return -1;
        } else
            return -65536;
    }

    public boolean getBooleanProperty(String name) {
        String p = getProperty(name);
        return p.equals("true");
    }

    public void setProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    public String getProperty(String name, String childAttributeName,
                              String childAttributeValue) {
        return StrUtil.getNullStr(properties.getProperty(name, childAttributeName,
                childAttributeValue));
    }

    public String getProperty(String name, String childAttributeName,
                              String childAttributeValue, String subChildName) {
        return StrUtil.getNullStr(properties.getProperty(name, childAttributeName,
                childAttributeValue, subChildName));
    }

    public void setProperty(String name, String childAttributeName,
                            String childAttributeValue, String value) {
        properties.setProperty(name, childAttributeName, childAttributeValue,
                value);
    }

    public void setProperty(String name, String childAttributeName,
                            String childAttributeValue, String subChildName,
                            String value) {
        properties.setProperty(name, childAttributeName, childAttributeValue,
                subChildName, value);
    }

    public boolean canUserLogin(HttpServletRequest request) throws ErrMsgException {
        if (-1 == cfg.getIntProperty("diskSpace")) {
            return true;
        }
        // 检查磁盘空间是否已超出
        if (cfg.getIntProperty("diskSpace") < cfg.getIntProperty("diskSpaceUsed")) {
            throw new ErrMsgException("磁盘空间已超出，请联系管理员！");
        }
        return true;
    }

}
