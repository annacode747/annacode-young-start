package cn.annacode.org.service;

import cn.annacode.youngStart.org.framework.annotation.Component;
import cn.annacode.youngStart.org.framework.interfaces.BeanPostProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class BeanPostProcessorService implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        // 该方法是批量处理
        // 初始化前
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        // 批量处理
        // 初始化后

        System.out.println("找到 ："+beanName);
        // 案例 设置代理对象
        if (beanName.equals("userServiceImpl")){
            // 假设userService运行就传一个动态代理对象
            // 动态代理Proxy 需要接口才能使用
            System.out.println("找到 使用："+beanName);
            Object proxyInstance = Proxy.newProxyInstance(BeanPostProcessorService.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // 切面逻辑
                    // 代理逻辑
                    System.out.println("代理模式: "+ beanName);
                    return method.invoke(bean,args);
                }
            });
            return proxyInstance;
        }
        return bean;
    }
}
