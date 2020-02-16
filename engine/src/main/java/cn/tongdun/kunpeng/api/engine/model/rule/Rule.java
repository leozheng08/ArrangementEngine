package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.tongdun.ddd.common.domain.Entity;
import cn.tongdun.kunpeng.api.engine.model.rule.function.WeightFunction;
import lombok.Data;

import java.util.List;

/**
 * 规则实体，规则的定义已经经过解析，可放到manager中执行。
 *
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
    /**
     * 规则引擎的可执行对象
     */
    private cn.fraudmetrix.module.tdrule.rule.Rule eval;
    /**
     * 定义一个tdrule的函数来计算规则的权重
     */
    private WeightFunction weightFunction;
}
