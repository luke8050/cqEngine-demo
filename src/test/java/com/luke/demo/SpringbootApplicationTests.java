package com.luke.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class SpringbootApplicationTests {
    private static final Logger logger = LoggerFactory.getLogger(SpringbootApplicationTests.class);

    @Autowired
    private ApplicationContext ctx;

    @Test
    void printBeanNames() {
        String[] beanNames = ctx.getBeanDefinitionNames();
        //String[] beanNames = ctx.getBeanNamesForAnnotation(RestController.class);//所有添加该注解的bean
        logger.info("bean总数: {}", ctx.getBeanDefinitionCount());
        int i = 0;
        for (String str : beanNames) {
            logger.info("{},beanName:{}", ++i, str);
        }
    }

}
