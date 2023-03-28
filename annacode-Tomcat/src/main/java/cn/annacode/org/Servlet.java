package cn.annacode.org;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Servlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getMethod());
        String ret = "hellow...";
//        resp.addHeader("Content-Length",Integer.toString(ret.length()));
//        resp.addHeader("Content-Type","text/plain;charset=utf-8");

        resp.getOutputStream().write(ret.getBytes());
    }
}
