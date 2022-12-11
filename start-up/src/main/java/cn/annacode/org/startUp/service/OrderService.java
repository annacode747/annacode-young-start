//package cn.annacode.service;
//
//import cn.annacode.youngStart.org.framework.annotation.Autowired;
//import cn.annacode.youngStart.org.framework.annotation.Component;
//import cn.annacode.youngStart.org.framework.interfaces.BeanNameAware;
//import cn.annacode.youngStart.org.framework.interfaces.InitializingBean;
//
//@Component
//public class OrderService implements BeanNameAware, InitializingBean {
//    @Autowired
//    private SheathService sheathService;
//
//    private String beanName;
//    public void order(){
//        sheathService.test();
//    }
//
//    @Override
//    public void setBeanName(String beanName) {
//        this.beanName = beanName;
//    }
//
//    @Override
//    public void afterPropertiesSet() {
//        // 初始化开启
//    }
//}
