package com.luke.demo.configuration;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/15 17:48
 **/
@Configuration
public class CacheConfig {

    /**
     * api方式加载ehcache
     * @return
     */
    /*@Bean
    public CacheManager cacheManager() {
        // 创建 CacheEventListenerConfiguration
        CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration = CacheEventListenerConfigurationBuilder
                .newEventListenerConfiguration(new MyCacheEventListener(), EventType.CREATED, EventType.UPDATED, EventType.EVICTED, EventType.REMOVED, EventType.EXPIRED)
                .unordered().asynchronous();

        return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("nodeCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                        String.class, Node.class,
                                        ResourcePoolsBuilder.heap(1000))
                                .withService(cacheEventListenerConfiguration)
                                .build())
                .withCache("ruleCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                        String.class, Rule.class,
                                        ResourcePoolsBuilder.heap(1000))
                                .build())
                .build(true); // true 表示初始化 CacheManager
    }*/

    /**
     * xml方式加载ehcache
     * @return
     */
    @Bean
    public CacheManager cacheManager() {
        URL myUrl = getClass().getResource("/ehcache.xml");
        org.ehcache.config.Configuration xmlConfig = new XmlConfiguration(myUrl);
        CacheManager myCacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        myCacheManager.init();
        return myCacheManager;
    }

}
