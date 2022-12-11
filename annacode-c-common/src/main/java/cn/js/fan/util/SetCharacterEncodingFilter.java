package cn.js.fan.util;

/**
 * <p>Title: 社区</p>
 * <p>Description: 社区</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 红月亮工作室</p>
 *
 * @author bluewind
 * @version 1.0
 */

import javax.servlet.*;
import java.io.IOException;

public class SetCharacterEncodingFilter
        implements Filter {

    protected String encoding = "GBK";

    protected FilterConfig filterConfig = null;

    public void setFilterConfig(FilterConfig config) {
        this.filterConfig = config;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void destroy() {

        this.encoding = null;
        this.filterConfig = null;

    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

// Select and set (if needed) the character encoding to be used
        String encoding = selectEncoding(request);
        if (encoding != null) {
            request.setCharacterEncoding(encoding);
            System.out.println("encoding:" + encoding);
        }

// Pass control on to the next filter
        chain.doFilter(request, response);

    }

    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        System.out.println(getClass() + " encoding...");
    }

    protected String selectEncoding(ServletRequest request) {

        return (this.encoding);
    }

}
