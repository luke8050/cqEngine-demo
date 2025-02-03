package com.luke.demo.service.impl;

import com.luke.demo.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.luke.demo.entity.Node;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @Description: NodeRepository使用
 * @author: lulu
 * @Date: 2025/1/14 23:50
 **/
@Service
public class NodeServiceDemo {

    @Autowired
    private NodeRepository nodeRepository;

    public List<Node> getAllNodes() {
        return nodeRepository.findAll();
    }

    public Node getNodeById(String nodeId) {
        return nodeRepository.findByNodeId(nodeId);
    }

    public Node createNode(Node node) {
        return nodeRepository.save(node);
    }

    public Node saveOrUpdateNode(Node node) {
        // 检查数据库中是否已存在
        Node existingNode = nodeRepository.findByNodeId(node.getNodeId());
        if (existingNode != null) {
            // 如果存在，更新现有数据
            Node updatedNode = existingNode;
            updatedNode.setName(node.getName());
            updatedNode.setNodeType(node.getNodeType());
            updatedNode.setAccountId(node.getAccountId());
            return nodeRepository.save(updatedNode);
        } else {
            // 如果不存在，插入新数据
            return nodeRepository.save(node);
        }
    }

    @Transactional
    public void deleteNode(String nodeId) {
        nodeRepository.deleteByNodeId(nodeId); // 删除数据库记录
    }
}
