package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

@Data
public class RuleDetail implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RuleDetail.class);

    private String ruleUuid;
    private String ruleId;
    private String ruleName;
    private String decision;
    private Double score;
    /**
     * 指标ID，关联指标详情的json信息
     */
    private List<String> partnerIndicatrixIds;
    /**
     * 指标ID, 带描述信息
     */
    private Map<String, Object> indicatrixDescripiton;

    private List<ConditionDetail> conditions;

    public RuleDetail() {
    }
}
