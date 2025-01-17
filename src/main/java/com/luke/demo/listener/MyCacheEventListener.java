package com.luke.demo.listener;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

/**
 * @Description: ehcache listener
 * @author: lulu
 * @Date: 2025/1/16 13:05
 **/
public class MyCacheEventListener implements CacheEventListener<Object, Object> {

    @Override
    public void onEvent(CacheEvent<? extends Object, ? extends Object> event) {
        System.out.println("Cache event: " + event.getType() + " Key: " + event.getKey() + " Old value: " + event.getOldValue() + " New value: " + event.getNewValue());
    }
}
