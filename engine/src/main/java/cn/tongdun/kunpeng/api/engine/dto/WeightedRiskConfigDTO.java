package cn.tongdun.kunpeng.api.engine.dto;

import lombok.Data;

/**
 * 权重匹配风险配置，内容为：{"mode":"Weighted","riskWeight":10,"weightRatio":20.33,"op":"+","property":{"type":"indicatrix/field","name":"指标/字段"},"propertyValue":{"value":"3333333333"},"upperLimitScore":-30,"lowerLimitScore":30}
 * @Author: liang.chen
 * @Date: 2020/2/23 下午12:13
 */
@Data
public class WeightedRiskConfigDTO{
    private double baseWeight = 0;                          // 风险权重基线
    private double weightRatio = 0;                         // 风险权重比例
    private String op;                                      // 四则运行操作符

    private String propertyType;                            // 变量类型类型 PLATFORM_INDEX/POLICY_INDEX/field
    private String propertyTypeName;                        // 变量名称
    private String property;                                // 变量值

    private Double upperLimitScore;                         // 权重计算分数右值上限
    private Double lowerLimitScore;                         // 权重计算分数右值下限

}
