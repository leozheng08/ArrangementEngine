package cn.tongdun.kunpeng.client.dto;

import lombok.Data;

/**
 * 权重匹配风险配置，内容为：{"mode":"Weighted","riskWeight":10,"weightRatio":20.33,"op":"+","property":{"type":"indicatrix/field","name":"指标/字段"},"propertyValue":{"value":"3333333333"},"upperLimitScore":-30,"lowerLimitScore":30}
 * @Author: liang.chen
 * @Date: 2020/2/23 下午12:13
 */
@Data
public class WeightedRiskConfigDTO{

    /**
     * 权重模式有下面的值
     */
    private Double baseWeight;

    /**
     * 权重比重
     */
    private Double weightRatio;

    /**
     * 权重属性
     *
     */
    private String weightProperty;

    /**
     * 权重属性值
     */
    private String weightPropertyValue;
    private Integer lowerLimitScore;
    private Integer upperLimitScore;

}
