package com.dpnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hooger on 2018/4/8.
 */

@SpringBootApplication
@EnableAutoConfiguration
public class BootApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(BootApplication.class, args);
    }
}
