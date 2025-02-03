package com.luke.demo.indexCache.impl;

import com.luke.demo.entity.Node;
import com.luke.demo.indexCache.AbstractCacheRepository;
import com.luke.demo.listener.MyCacheEventListener;
import com.luke.demo.repository.NodeRepository;
import org.ehcache.CacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.EnumSet;
import java.util.Optional;

/**
 * @Description: 不带索引的ehcache
 * @author: lulu
 * @Date: 2025/1/17 16:05
 **/
//@Service
public class NodeCacheRepository extends AbstractCacheRepository<Node, String> {

    private static final Logger logger = LoggerFactory.getLogger(NodeCacheRepository.class);

    private static final String CACHE_NAME = "nodeCache";

    public NodeCacheRepository(CacheManager cacheManager, JpaRepository<Node, String> repository) {
        super(CACHE_NAME, cacheManager.getCache(CACHE_NAME, String.class, Node.class), repository);
    }

    @Override
    public Node findByKeyId(String s) {
        // 从缓存中获取实体, 如果缓存中不存在，则从数据库中获取并更新缓存
        return Optional.ofNullable(cache.get(s))
                .orElseGet(() -> {
                    Node entity = ((NodeRepository)repository).findByNodeId(s);
                    if (entity != null) {
                        cache.put(entity.getCacheKey(), entity);
                    }
                    return entity;
                });
    }

    @Override
    @Transactional
    public void deleteByKeyId(String id) {
        ((NodeRepository)repository).deleteByNodeId(id);
        cache.remove(id);
    }

    @Override
    protected void afterInitializeCache() {
        registerEventListener();
    }

    // 延迟注册监听器
    private void registerEventListener() {
        // 注册事件监听器
        cache.getRuntimeConfiguration().registerCacheEventListener(
                new MyCacheEventListener(), // 自定义事件监听器
                EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS,
                EnumSet.of(EventType.CREATED, EventType.REMOVED, EventType.EVICTED, EventType.EXPIRED, EventType.UPDATED)
        );

        logger.info("registerEventListener【{}】done!", CACHE_NAME);
    }
}
