package cn.annacode.org.service.impl;

import cn.annacode.org.service.OrderService;
import cn.annacode.org.service.UserService;
import cn.annacode.youngStart.org.framework.annotation.Autowired;
import cn.annacode.youngStart.org.framework.annotation.Component;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private OrderService orderService;
    @Override
    public void test(){
        System.out.println("很简单的，毫无意义的测试方法");
        orderService.order();
    }
}
