package com.luke.demo.entity;

import com.luke.demo.constant.NodeType;
import com.luke.demo.indexCache.CacheKey;
import com.luke.demo.indexCache.MergeEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/14 23:45
 **/
@Entity
@Table(name = "node")
public class Node implements CacheKey<String>, MergeEntity<Node> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id", unique = true, nullable = false)
    private String nodeId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false)
    private NodeType nodeType;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 自动设置创建时间
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getCacheKey() {
        return nodeId;
    }

    @Override
    public Node merge(Node node) {
        if (node != null) {
            this.name = node.getName();
            this.nodeType = node.getNodeType();
            this.accountId = node.getAccountId();
            this.createdAt = node.getCreatedAt();
        }
        return this;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", name='" + name + '\'' +
                ", nodeType=" + nodeType +
                ", accountId=" + accountId +
                ", createdAt=" + createdAt +
                '}';
    }
}

