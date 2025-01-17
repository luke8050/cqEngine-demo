package com.luke.demo.repository;

import com.luke.demo.entity.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description: 根据jpa规范自定义的查询方法，是通过动态代理实现的接口方法
 * @author: lulu
 * @Date: 2025/1/14 23:48
 **/
@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Node findByNodeId(String nodeId);

    void deleteByNodeId(String nodeId);
}
