package cn.annacode.org.startUp;

import cn.annacode.org.framework.config.ApplicationStartedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class test {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(test.class);
        ApplicationStartedEventListener asel = new ApplicationStartedEventListener();
        app.addListeners(asel);
        System.out.println(app.getWebApplicationType());
//        app.set
//        app.run(args);
    }
}
