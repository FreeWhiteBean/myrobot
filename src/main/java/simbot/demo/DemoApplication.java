package simbot.demo;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类。
 */
@EnableSimbot
@SpringBootApplication
@MapperScan("simbot.demo.dao")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
