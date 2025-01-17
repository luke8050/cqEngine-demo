package com.luke.demo.controller;

import com.luke.demo.entity.Node;
import com.luke.demo.service.impl.NodeCacheServiceDemo;
import com.luke.demo.service.impl.NodeServiceDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/14 23:52
 **/
@RestController
@RequestMapping("/nodes")
public class NodeController {

    @Autowired
    private NodeServiceDemo nodeService;
    @Autowired
    private NodeCacheServiceDemo nodeCacheService;

    @GetMapping("/getAllNodes")
    public List<Node> getAllNodes() {
        return nodeService.getAllNodes();
    }

    @GetMapping("/getNodeById/{nodeId}")
    public Node getNodeById(@PathVariable String nodeId) {
//        return nodeService.getNodeById(nodeId);
        return nodeCacheService.getNodeById(nodeId);
    }

    @GetMapping("/deleteNode/{nodeId}")
    public void deleteNode(@PathVariable String nodeId) {
//        return nodeService.getNodeById(nodeId);
        nodeCacheService.deleteNode(nodeId);
    }

    @PostMapping("/createNode")
    public Node createNode(@RequestBody Node node) {
//        return nodeService.createNode(node);
//        return nodeService.saveOrUpdateNode(node);
        return nodeCacheService.saveOrUpdateNode(node);
    }
}
