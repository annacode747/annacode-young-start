package com.cloudwebsoft.framework.util;

import cn.js.fan.util.StrUtil;
import cn.js.fan.util.XMLProperties;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

public class XMLConfig {
    public String xmlFileName;

    XMLProperties properties;
    Document doc = null;
    Element root = null;

    public XMLConfig(String xmlFileName) {
        this.xmlFileName = xmlFileName;
        init();
    }

    public void init() {
        URL cfgURL = getClass().getResource("/" + xmlFileName);
        String cfgpath = URLDecoder.decode(cfgURL.getFile());
        properties = new XMLProperties(cfgpath);

        SAXBuilder sb = new SAXBuilder();
        try {
            FileInputStream fin = new FileInputStream(cfgpath);
            doc = sb.build(fin);
            root = doc.getRootElement();
            fin.close();
        } catch (JDOMException e) {
            LogUtil.getLog(getClass()).error("init1:" + e.getMessage());
        } catch (IOException e) {
            LogUtil.getLog(getClass()).error("init2:" + e.getMessage());
        }
    }

    public Document getDocument() {
        return doc;
    }

    public Element getRoot() {
        return root;
    }

    public String getProperty(String name) {
        return StrUtil.getNullStr(properties.getProperty(name));
    }

    public int getIntProperty(String name) {
        String p = getProperty(name);
        if (StrUtil.isNumeric(p)) {
            return Integer.parseInt(p);
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
}
