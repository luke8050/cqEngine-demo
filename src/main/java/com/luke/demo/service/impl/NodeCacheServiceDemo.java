package com.luke.demo.service.impl;

import com.luke.demo.listener.MyCacheEventListener;
import com.luke.demo.entity.Node;
import com.luke.demo.repository.NodeRepository;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.EnumSet;
import java.util.List;

/**
 * @Description: NodeRepository结合ehcache使用
 * @author: lulu
 * @Date: 2025/1/14 23:50
 **/
//@Service
public class NodeCacheServiceDemo {

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private CacheManager cacheManager;

    private Cache<String, Node> nodeCache;

    /**
     * 初始化时加载缓存、注册监听器
     */
    @PostConstruct
    public void initializeCache() {
        nodeCache = cacheManager.getCache("nodeCache", String.class, Node.class);
        // 从数据库加载所有数据到缓存
        getAllNodes().forEach(node -> nodeCache.put(node.getNodeId(), node));

        registerEventListener();
    }

    // 延迟注册监听器
    private void registerEventListener() {
        // 注册事件监听器
        nodeCache.getRuntimeConfiguration().registerCacheEventListener(
                new MyCacheEventListener(), // 自定义事件监听器
                EventOrdering.ORDERED, EventFiring.ASYNCHRONOUS,
                EnumSet.of(EventType.CREATED, EventType.REMOVED, EventType.EVICTED, EventType.EXPIRED, EventType.UPDATED)
        );
    }

    public List<Node> getAllNodes() {
        return nodeRepository.findAll();
    }

    public Node getNodeById(String nodeId) {
        // 先从缓存中查找
        Node node = nodeCache.get(nodeId);
        if (node == null) {
            // 如果缓存中没有，从数据库中查找
            node = nodeRepository.findByNodeId(nodeId);
            if (node!= null) {
                // 找到后添加到缓存
                nodeCache.put(nodeId, node);
            }
        }
        return node;
    }

    /**
     * 更新 Node 数据并同步到缓存
     */
    public Node saveOrUpdateNode(Node node) {
        Node updatedNode;
        // 检查数据库中是否已存在
        Node existingNode = nodeRepository.findByNodeId(node.getNodeId());
        if (existingNode != null) {
            // 如果存在，更新现有数据
            updatedNode = existingNode;
            updatedNode.setName(node.getName());
            updatedNode.setNodeType(node.getNodeType());
            updatedNode.setAccountId(node.getAccountId());
            nodeRepository.save(updatedNode);
        } else {
            // 如果不存在，插入新数据
            updatedNode = nodeRepository.save(node);
        }
        nodeCache.put(node.getNodeId(), updatedNode); // 更新缓存
        return updatedNode;
    }

    /**
     * 删除 Node 数据并同步删除缓存
     */
    @Transactional
    public void deleteNode(String nodeId) {
        nodeRepository.deleteByNodeId(nodeId); // 删除数据库记录
        nodeCache.remove(nodeId); // 删除缓存
    }
}
