package cn.annacode.org.service;

import cn.annacode.youngStart.org.framework.annotation.Autowired;
import cn.annacode.youngStart.org.framework.annotation.Component;
import cn.annacode.youngStart.org.framework.annotation.Lazy;
import cn.annacode.youngStart.org.framework.annotation.Scope;
import cn.annacode.youngStart.org.framework.enumerate.ScopeType;

@Component
@Scope(ScopeType.PROTOTYPE)
public class UserService {

    @Autowired
    private OrderService orderService;
    public void test(){
        System.out.println("很简单的，毫无意义的测试方法");
        System.out.println(orderService);
    }
}
