package cn.annacode.org;

import cn.annacode.org.service.UserService;
import cn.annacode.youngStart.org.framework.ApplicationContext;
import cn.annacode.youngStart.org.framework.config.AppConfig;

public class MyApplication {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        UserService userService =  (UserService) applicationContext.getBean("userService");
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
//        userService.test();
    }
}
