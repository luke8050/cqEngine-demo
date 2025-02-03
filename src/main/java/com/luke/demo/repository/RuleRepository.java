package com.luke.demo.repository;

import com.luke.demo.entity.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description:
 * @author: lulu
 * @Date: 2025/1/14 23:49
 **/
public interface RuleRepository extends JpaRepository<Rule, String> {
    Rule findByRuleId(String ruleId);
}
