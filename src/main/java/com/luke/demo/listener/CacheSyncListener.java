package com.luke.demo.listener;

import com.luke.demo.indexCache.CQEngineCache;
import com.luke.demo.indexCache.CacheKey;
import com.luke.demo.indexCache.IndexedCacheRepository;
import com.luke.demo.indexCache.MergeEntity;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @Description: 组合设计模式，监听ehcache事件，同步到CQEngine
 * @author: lulu
 * @Date: 2025/1/24 15:48
 **/
public class CacheSyncListener<T extends CacheKey<ID> & MergeEntity<T>, ID, M extends CacheKey<ID>> implements CacheEventListener<Object, T> {

    private static final Logger logger = LoggerFactory.getLogger(CacheSyncListener.class);

    private final CQEngineCache<M, ID> cqEngineCache;
    private final IndexedCacheRepository<T, ID, M> indexedCacheRepository;

    public CacheSyncListener(CQEngineCache<M, ID> cqEngineCache, IndexedCacheRepository<T, ID, M> indexedCacheRepository) {
        this.cqEngineCache = cqEngineCache;
        this.indexedCacheRepository = indexedCacheRepository;
    }

    @Override
    public void onEvent(CacheEvent<? extends Object, ? extends T> cacheEvent) {
        switch (cacheEvent.getType()) {
            case CREATED:
            case UPDATED:
                // 缓存添加或更新时，同步到CQEngine，通过先删除旧数据再添加新数据的方式实现更新
                M m = indexedCacheRepository.getMetaInfo(cacheEvent.getNewValue());
                if (Objects.nonNull(m)) {
                    cqEngineCache.remove(m);
                }
                cqEngineCache.add(indexedCacheRepository.createMetaInfo(cacheEvent.getNewValue()));
                logger.info("CacheSyncListener On Cache event: Added/Updated item in CQEngine - {}", cacheEvent.getNewValue());
                break;
            case REMOVED:
            case EXPIRED:
                // 缓存删除或过期时，从CQEngine中移除
                cqEngineCache.remove(indexedCacheRepository.getMetaInfo(cacheEvent.getOldValue()));
                logger.info("CacheSyncListener On Cache event: Removed item from CQEngine - {}", cacheEvent.getOldValue());
                break;
            default:
                break;
        }
    }
}
