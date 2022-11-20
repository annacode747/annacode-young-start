package cn.annacode.youngStart.org.framework.interfaces;

import cn.annacode.youngStart.org.framework.ApplicationContext;

/**
 *  获取ApplicationContext对象
 */
public interface ApplicationContextAware {
    void setApplicationContext(ApplicationContext applicationContext);
}
