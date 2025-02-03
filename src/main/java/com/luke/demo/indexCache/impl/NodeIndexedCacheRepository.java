package com.luke.demo.indexCache.impl;

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.QueryFactory;
import com.googlecode.cqengine.resultset.ResultSet;
import com.luke.demo.entity.Node;
import com.luke.demo.entity.NodeMetaInfo;
import com.luke.demo.indexCache.CQEngineCache;
import com.luke.demo.indexCache.IndexedCacheRepository;
import com.luke.demo.repository.NodeRepository;
import org.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 通过CQEngine实现索引缓存，通过CQEngine的索引来加速查询
 * @author: lulu
 * @Date: 2025/1/20 00:42
 **/
@Service
public class NodeIndexedCacheRepository extends IndexedCacheRepository<Node, String, NodeMetaInfo> {

    private static final Logger logger = LoggerFactory.getLogger(NodeIndexedCacheRepository.class);

    private static final String CACHE_NAME = "nodeCache";

    public NodeIndexedCacheRepository(CacheManager cacheManager, JpaRepository<Node, String> repository) {
        super(CACHE_NAME, cacheManager.getCache(CACHE_NAME, String.class, Node.class), repository, new CQEngineCache<NodeMetaInfo, String>());
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
    public void createIndex() {
        // 初始化索引
        cqEngineCache.addIndex(NodeMetaInfo.NODE_ID);
        cqEngineCache.addIndex(NodeMetaInfo.ACCOUNT_ID);
    }

    @Override
    public NodeMetaInfo createMetaInfo(Node node) {
        return new NodeMetaInfo(node);
    }

    @Override
    public Query<NodeMetaInfo> getKeyEqualQuery(CQEngineCache<NodeMetaInfo, String> cqEngineCache, Node node) {
        return QueryFactory.equal(NodeMetaInfo.NODE_ID, node.getCacheKey());
    }

    public Node getNodeById(String nodeId) {
        // 先查cqengine缓存, 得到cacheKey，在查询ehcache
        Query<NodeMetaInfo> query = QueryFactory.equal(NodeMetaInfo.NODE_ID, nodeId);
        ResultSet<NodeMetaInfo> resultSet = cqEngineCache.search(query);
        NodeMetaInfo nodeMetaInfo = resultSet.size() == 1 ? resultSet.uniqueResult() : null;
        return nodeMetaInfo == null ? null : cache.get(nodeMetaInfo.getCacheKey());
    }

    public List<Node> getNodesWithAccountIdGreaterThan(Integer accountId) {
        Query<NodeMetaInfo> query = QueryFactory.greaterThan(NodeMetaInfo.ACCOUNT_ID, accountId);
        ResultSet<NodeMetaInfo> resultSet =  cqEngineCache.search(query);
        return resultSet.stream()
                .map(nodeMetaInfo -> cache.get(nodeMetaInfo.getCacheKey()))
                .collect(Collectors.toList());
    }

    public void testCQEngine() {
        // 测试cqengine的查询API
        Query<NodeMetaInfo> query = QueryFactory.contains(NodeMetaInfo.NODE_ID, "N01");
        cqEngineCache.search(query).forEach(nodeMetaInfo -> System.out.println(cache.get(nodeMetaInfo.getCacheKey())));

        Query<NodeMetaInfo> query1 = QueryFactory.between(NodeMetaInfo.ACCOUNT_ID, 115, 118);
        cqEngineCache.search(query1).forEach(nodeMetaInfo -> System.out.println(cache.get(nodeMetaInfo.getCacheKey())));

        Query<NodeMetaInfo> query2 = QueryFactory.and(query, query1);
        cqEngineCache.search(query2).forEach(nodeMetaInfo -> System.out.println(cache.get(nodeMetaInfo.getCacheKey())));
    }

}
