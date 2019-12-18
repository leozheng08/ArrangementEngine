package cn.tongdun.kunpeng.api.rule;

import cn.tongdun.ddd.domain.Entity;
import lombok.Data;

/**
 * 规则实体，规则的定义已经经过解析，可放到manager中执行。
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:20
 */
@Data
public class Rule extends Entity {

    private String ruleId;
    private String ruleCustomId;
    private String uuid;
    private String parentUuid;
    private String subPolicyUuid;

    private String name;
    //决策结果
    private String decision;
    private Integer displayOrder;
    private Boolean pilotRun;
    private String template;

    private cn.fraudmetrix.module.tdrule.rule.Rule eval;
}
