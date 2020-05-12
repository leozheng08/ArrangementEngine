package cn.tongdun.kunpeng.api.engine.dto;

import lombok.Data;

/**
 * 最坏匹配风险配置，内容为：{"mode":"WorstMatch","riskDecision":"Accept"}
 * @Author: liang.chen
 * @Date: 2020/2/23 下午12:13
 */
@Data
public class WorstMatchRiskConfigDTO extends RiskConfigDTO{
    private String riskDecision;                            // 风险决策结果
}
