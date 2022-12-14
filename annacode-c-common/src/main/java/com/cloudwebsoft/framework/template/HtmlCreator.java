package com.cloudwebsoft.framework.template;

import cn.js.fan.util.ErrMsgException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Title: 生成HTML文件</p>
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
public class HtmlCreator {
    public HtmlCreator() {
    }

    public void service(HttpServletRequest request,
                        HttpServletResponse response, String filePath) throws
            IOException, ServletException {
        /*
        java.util.Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String param = (String) e.nextElement();
            // 接下来对元素的操作
        }
        */
        // System.out.print(getClass().getName() + " filePath=" + filePath);
        try {
            TemplateLoader tl = new TemplateLoader(request, filePath);
            String pageContent = tl.toString();
            // System.out.print(getClass().getName() + " pageContent=" + pageContent);
            response.getWriter().write(pageContent);
        } catch (ErrMsgException e) {
            System.out.print(getClass() + e.getMessage());
        }
    }
}
