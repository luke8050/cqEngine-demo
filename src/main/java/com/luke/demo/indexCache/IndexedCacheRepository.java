package com.luke.demo.indexCache;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import com.luke.demo.listener.CacheSyncListener;
import org.ehcache.Cache;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.EnumSet;


/**
 * @Description: IndexedCacheRepository继承AbstractCacheRepository，并使用组合设计模式，拓展了cqEngineCache
 * T-cache entity, ID-cacheKey, M-metaInfo
 * @author: lulu
 * @Date: 2025/1/20 00:33
 **/
public abstract class IndexedCacheRepository<T extends CacheKey<ID> & MergeEntity<T>, ID, M extends CacheKey<ID>>
                            extends AbstractCacheRepository<T, ID>{
    private static final Logger logger = LoggerFactory.getLogger(IndexedCacheRepository.class);

    protected final CQEngineCache<M, ID> cqEngineCache;

    public IndexedCacheRepository(String cacheName, Cache<ID, T> cache, JpaRepository<T, ID> repository, CQEngineCache<M, ID> cqEngineCache) {
        super(cacheName, cache, repository);
        this.cqEngineCache = cqEngineCache;
    }

    /**
     * 创建索引
     */
    public abstract void createIndex();

    /**
     * 根据实体对象创建metaInfo
     * @param t
     * @return
     */
    public abstract M createMetaInfo(T t);

    /**
     * 获取metaInfo查询条件
     * @param cqEngineCache
     * @param t
     * @return
     */
    public abstract Query<M> getKeyEqualQuery(CQEngineCache<M, ID> cqEngineCache, T t);

    @Override
    protected void afterInitializeCache() {
        // 初始化索引
        createIndex();

        // 加载metaInfo数据到cqEngine
        getAll().forEach(item -> cqEngineCache.add(createMetaInfo(item)));

        // 注册监听器
        registerEventListener();
        logger.info("【{}】afterInitializeCache done!", cacheName);

    }

    /**
     * 根据缓存对象查询metaInfo
     * @param t
     * @return
     */
    public M getMetaInfo(T t) {
        Query<M> query = getKeyEqualQuery(cqEngineCache, t);
        ResultSet<M> resultSet = cqEngineCache.search(query);
        if (resultSet.size() == 1) {
            return resultSet.uniqueResult();
        } else {
            return null;
        }
    }

    // 延迟注册监听器
    private void registerEventListener() {
        // 注册事件监听器
        cache.getRuntimeConfiguration().registerCacheEventListener(
                new CacheSyncListener<>(cqEngineCache, this), // 自定义事件监听器
                EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS,
                EnumSet.of(EventType.CREATED, EventType.REMOVED, EventType.EVICTED, EventType.EXPIRED, EventType.UPDATED)
        );

        logger.info("【{}】registerEventListener done!", cacheName);
    }
}
