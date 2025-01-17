package com.luke.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class SpringbootApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApp.class, args);
    }

}
