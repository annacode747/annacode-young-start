package cn.annacode.org.service;

import cn.annacode.youngStart.org.framework.annotation.Autowired;
import cn.annacode.youngStart.org.framework.annotation.Component;

@Component
public class SheathService {
    @Autowired
    private Sheath2Service sheath2Service;
    public void test(){
        sheath2Service.test();
    }
}
