package com.luke.demo.entity;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.luke.demo.indexCache.CacheKey;

/**
 * @Description: Node表关键字段作为cqEngine索引
 * @author: lulu
 * @Date: 2025/1/23 16:54
 **/
public class NodeMetaInfo implements CacheKey<String> {
    private String nodeId;

    private Integer accountId;

    public NodeMetaInfo(Node node) {
        this.nodeId = node.getNodeId();
        this.accountId = node.getAccountId();
    }

    @Override
    public String getCacheKey() {
        return nodeId;
    }

    public static final Attribute<NodeMetaInfo, String> NODE_ID = new SimpleAttribute<NodeMetaInfo, String>("nodeId") {
        public String getValue(NodeMetaInfo node, QueryOptions queryOptions) { return node.getNodeId(); }
    };

    public static final Attribute<NodeMetaInfo, Integer> ACCOUNT_ID = new SimpleAttribute<NodeMetaInfo, Integer>("accountId") {
        public Integer getValue(NodeMetaInfo node, QueryOptions queryOptions) { return node.getAccountId(); }
    };

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
}
