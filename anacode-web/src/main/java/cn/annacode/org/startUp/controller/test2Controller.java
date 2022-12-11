package cn.annacode.org.startUp.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class test2Controller {
    @GetMapping("/ffef")
    public void f() {
        log.info("info");
        log.error("error");
        log.warn("warn");
    }
}
