package cn.js.fan.util;

import org.apache.log4j.PropertyConfigurator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Log4jInit extends HttpServlet {
    private static final String CONTENT_TYPE = "text/html; charset=GBK";

    //Initialize global variables
    @Override
    public void init() throws ServletException {
        String realPath = getServletContext().getRealPath("/");
        // weblogic
        // System.out.println(getClass() + " Log4jInit:realPath=" + realPath + " File.separator=" + File.separator);
        if (realPath.lastIndexOf(File.separator) != realPath.length() - 1)
            realPath += File.separator;
        // System.out.println(getClass() + " Log4jInit:realPath=" + realPath);

        String file = getInitParameter("log4j"); //配置文件位置
        if (file != null) {
            PropertyConfigurator.configure(realPath + file);
        }
    }

    //Process the HTTP Get request
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>Log4jInit</title></head>");
        out.println("<body bgcolor=\"#ffffff\">");
        out.println("<p>The servlet has received a GET. This is the reply.</p>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    //Clean up resources
    @Override
    public void destroy() {
    }
}
