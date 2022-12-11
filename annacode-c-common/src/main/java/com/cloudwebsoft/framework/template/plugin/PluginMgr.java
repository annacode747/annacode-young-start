package com.cloudwebsoft.framework.template.plugin;

import cn.js.fan.cache.jcs.RMCache;
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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PluginMgr {
    RMCache rmCache;
    final String group = "TEMPALTE_PLUGIN";
    final String ALLPLUGIN = "TEMPALTE_ALLPLUGIN";

    static Logger logger;
    public final String FILENAME = "plugin_template.xml";

    public static Document doc = null;
    public static Element root = null;
    public static String xmlPath;
    public static boolean isInited = false;
    public static URL confURL;

    public PluginMgr() {
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

    public PluginUnit getPluginUnit(String code) {
        PluginUnit pu = null;
        try {
            pu = (PluginUnit) rmCache.getFromGroup(code, group);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (pu == null) {
            init();
            List list = root.getChildren();
            if (list != null) {
                Iterator ir = list.iterator();
                while (ir.hasNext()) {
                    Element child = (Element) ir.next();
                    String ecode = child.getAttributeValue("code");
                    if (ecode.equals(code)) {
                        String desc = child.getChildText("desc");
                        String classUnit = child.getChildText("classUnit");
                        pu = new PluginUnit(code);
                        pu.setClassUnit(classUnit);
                        pu.setDesc(desc);
                        try {
                            rmCache.putInGroup(code, group,
                                    pu);
                        } catch (Exception e) {
                            logger.error("getPluginUnit:" + e.getMessage());
                        }
                        return pu;
                    }
                }
            }
        } else {
            pu.renew();
        }
        return pu;
    }

    public Vector getAllPlugin() {
        Vector v = null;
        try {
            v = (Vector) rmCache.getFromGroup(ALLPLUGIN, group);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (v == null) {
            v = new Vector();
            init();
            List list = root.getChildren();
            if (list != null) {
                Iterator ir = list.iterator();
                while (ir.hasNext()) {
                    Element child = (Element) ir.next();
                    String code = child.getAttributeValue("code");
                    v.addElement(getPluginUnit(code));
                }
                try {
                    rmCache.putInGroup(ALLPLUGIN, group, v);
                } catch (Exception e) {
                    logger.error("getAllPlugin:" + e.getMessage());
                }
            }
        }
        return v;
    }

    public void writemodify() {
        String indent = "    ";
        Format format = Format.getPrettyFormat();
        format.setIndent(indent);
        format.setEncoding("utf-8");
        XMLOutputter outp = new XMLOutputter(format);
        try {
            FileOutputStream fout = new FileOutputStream(xmlPath);
            outp.output(doc, fout);
            fout.close();
        } catch (java.io.IOException e) {
        }
    }
}
