package com.cloudweb.oa.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

@Component
@WebListener
@Slf4j
public class SpringUtil implements ApplicationContextAware, ServletContextListener {

    private static ApplicationContext applicationContext;

    private static ServletContext servletContext;

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        applicationContext = arg0;
        System.out.println("---------------com.cloudweb.oa.utils.SpringUtil setApplicationContext---------------");
    }

    public static Object getBean(String name) throws BeansException {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        // System.out.println(SpringUtil.class.getName() + " applicationContext=" + applicationContext);
        return getApplicationContext().getBean(requiredType);
    }

    public static String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static DataSource getDataSource() {
        return getApplicationContext().getBean(DataSource.class);
    }

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static ServletContext getServletContext() {
        // ??????tomcat????????????servletContext??????null??????dwr?????????????????????????????????????????????
        if (servletContext == null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            servletContext = request.getServletContext();
        }
        return servletContext;
    }

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            applicationContext = (ApplicationContext) getServletContext().getAttribute(CommonConstUtil.APPLICATION_CONTEXT);
        }
        return applicationContext;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        if (servletContext == null) {
            log.info("??????????????????????????????......");
            WebApplicationContext webApplicationContext = WebApplicationContextUtils
                    .getWebApplicationContext(servletContextEvent.getServletContext());
            servletContext = servletContextEvent.getServletContext();
            // servletContext.setAttribute(CommonConstUtil.DATA_SOURCE, webApplicationContext.getBean(DataSource.class));

            servletContext.setAttribute(CommonConstUtil.APPLICATION_CONTEXT, applicationContext);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().removeAttribute(CommonConstUtil.APPLICATION_CONTEXT);
    }
}