package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import lombok.Data;

import java.io.Serializable;

/**
 * 条件详情父类
 *
 * @Author: liang.chen
 * @Date: 2020/2/5 下午8:05
 */
@Data
public class ConditionDetail implements Serializable, IDetail {

    /**
     * 条件uuid:rule_condition_element uuid
     */
    private String conditionUuid;

    /**
     * 规则uuid:rule uuid
     */
    private String ruleUuid;

    /**
     * 详情类型，不同的详细类型有对应的子类
     */
    private String type;

    /**
     * 描述
     */
    private String description;

    public ConditionDetail() {
    }

    public ConditionDetail(String type) {
        this.type = type;
    }
}
