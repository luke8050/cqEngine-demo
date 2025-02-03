package com.luke.demo.entity;

import com.luke.demo.constant.RuleType;
import com.luke.demo.indexCache.CacheKey;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/14 23:47
 **/
@Entity
@Table(name = "rules")
public class Rule implements CacheKey<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", unique = true, nullable = false)
    private String ruleId;

    @Column(name = "version_id", nullable = false)
    private Integer versionId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "rule_type", nullable = false)
    private RuleType ruleType;

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

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType ruleType) {
        this.ruleType = ruleType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String getCacheKey() {
        return ruleId + ":" + versionId;
    }
}

