package cn.annacode.org.service;

import cn.annacode.youngStart.org.framework.annotation.Autowired;
import cn.annacode.youngStart.org.framework.annotation.Component;

@Component
public class ConstructionMethodTest {
//    @Autowired
    private OrderService orderService;

    public ConstructionMethodTest(OrderService orderService) {
        this.orderService = orderService;
    }
    public void test(){
        System.out.println(orderService);
    }
}
