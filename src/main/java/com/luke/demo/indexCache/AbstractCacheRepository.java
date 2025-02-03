package com.luke.demo.indexCache;

import java.util.List;
import java.util.Objects;

import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.PostConstruct;

/**
 * @Description: T-cache entity, ID-cacheKey
 * 使用 & 符号来为泛型接口指定多个上界
 * @author: lulu
 * @Date: 2025/1/17 14:33
 **/
public abstract class AbstractCacheRepository<T extends CacheKey<ID> & MergeEntity<T>, ID>
                                            implements BaseRepository<T, ID> {
    protected String cacheName;
    protected Cache<ID, T> cache;
    protected JpaRepository<T, ID> repository;

    private static final Logger logger = LoggerFactory.getLogger(AbstractCacheRepository.class);

    public AbstractCacheRepository(String cacheName, Cache<ID, T> cache, JpaRepository<T, ID> repository) {
        this.cacheName = Objects.requireNonNull(cacheName);
        this.cache = Objects.requireNonNull(cache);
        this.repository = Objects.requireNonNull(repository);
    }

    @PostConstruct
    public void initializeCache() {
        // 从数据库加载所有数据到缓存
        getAll().forEach(item -> cache.put(item.getCacheKey(), item));
        logger.info("initializeCache【{}】done!", cacheName);

        // 初始化完成之后的动作
        afterInitializeCache();
    }

    protected void afterInitializeCache() {
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public T insert(T t) {
        // 保存更新后的实体到数据库
        repository.save(t);

        // 更新缓存中的实体
        cache.put(t.getCacheKey(), t);

        return t;
    }

    @Override
    public boolean update(T t) {
        // 从缓存中获取实体
        T existingEntity = findByKeyId(t.getCacheKey());

        if (existingEntity == null) {
            // 如果实体不存在，返回0表示更新失败
            return false;
        }

        // 更新实体的属性
        // 这里假设T类有一个merge方法来合并属性，如果没有，需要手动设置每个属性
        existingEntity.merge(t);

        // 保存更新后的实体到数据库
        insert(existingEntity);

        // 返回true表示更新成功
        return true;
    }

}
