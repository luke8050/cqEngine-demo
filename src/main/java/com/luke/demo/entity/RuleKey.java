package com.luke.demo.entity;

import java.io.Serializable;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/16 15:14
 **/
public class RuleKey implements Serializable {
    private String rule_id;
    private Integer version_id;

    public RuleKey(String rule_id, Integer version_id) {
        this.rule_id = rule_id;
        this.version_id = version_id;
    }

    // Getters and setters
    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public Integer getVersion_id() {
        return version_id;
    }

    public void setVersion_id(Integer version_id) {
        this.version_id = version_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass()!= o.getClass()) return false;
        RuleKey ruleKey = (RuleKey) o;
        return rule_id.equals(ruleKey.rule_id) && version_id.equals(ruleKey.version_id);
    }

    @Override
    public int hashCode() {
        int result = rule_id.hashCode();
        result = 31 * result + version_id.hashCode();
        return result;
    }
}
