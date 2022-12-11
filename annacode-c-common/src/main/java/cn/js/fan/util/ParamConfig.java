package cn.js.fan.util;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Vector;

public class ParamConfig {
    // public: constructor to load driver and connect db
    private XMLProperties properties;
    private String fileName;
    private String filePath;
    Logger logger;
    Document doc = null;
    Element root = null;

    String rootChild = "";
    String encoding = "utf-8";

    public ParamConfig(String fileName) {
        this.fileName = fileName;
        URL cfgURL = getClass().getResource("/" + fileName);

        filePath = cfgURL.getFile();
        filePath = URLDecoder.decode(filePath);

        File file = new File(filePath);

        logger = Logger.getLogger(ParamConfig.class.getName());

        SAXBuilder sb = new SAXBuilder();
        try {
            doc = sb.build(file);
            root = doc.getRootElement();
            properties = new XMLProperties(file, doc);
        } catch (JDOMException e) {
            logger.error("XMLConfig:" + e.getMessage());
        } catch (IOException e) {
            logger.error("XMLConfig:" + e.getMessage());
        }
    }

    public Element getRootElement() {
        return root;
    }

    public FormRule getFormRule(String code) {
        Iterator ir = root.getChildren().iterator();
        Vector[] v = new Vector[2];
        v[0] = new Vector();
        v[1] = new Vector();
        boolean onErrorExit = false;
        String res = "";

        boolean isFinded = false;

        while (ir.hasNext()) {
            Element e = (Element) ir.next();
            String c = e.getAttributeValue("code");
            if (c.equals(code)) {
                res = StrUtil.getNullStr(e.getAttributeValue("res"));
                onErrorExit = StrUtil.getNullStr(e.getAttributeValue("onErrorExit")).equals("true");
                Element e1 = e.getChild("rules");
                Iterator ir1 = e1.getChildren().iterator();
                while (ir1.hasNext()) {
                    String t = ((Element) ir1.next()).getText();
                    v[0].addElement(t);
                }
                Element e2 = e.getChild("unionRules");
                Iterator ir2 = e2.getChildren().iterator();
                while (ir2.hasNext()) {
                    String t = ((Element) ir2.next()).getText();
                    v[1].addElement(t);
                }
                isFinded = true;
            }
        }

        if (!isFinded)
            return null;

        FormRule fr = new FormRule();

        fr.setOnErrorExit(onErrorExit);
        fr.setRules(v[0]);
        fr.setUnionRules(v[1]);
        fr.setRes(res);
        return fr;
    }

    public void writemodify() {
        String indent = "    ";
        Format format = Format.getPrettyFormat();
        format.setIndent(indent);
        format.setEncoding(encoding);
        XMLOutputter outp = new XMLOutputter(format);
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            outp.output(doc, fout);
            fout.close();
        } catch (IOException e) {
        }
    }
}
