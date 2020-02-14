package cn.tongdun.kunpeng.api.engine.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 规则
 * 
 * @author du 2014年2月15日 下午5:45:09
 */
@Data
public class RuleDTO extends CommonDTO {

    private static final long            serialVersionUID = -2526030344227360438L;

    private String ruleCustomId;

    private String name;

    /**
     * 状态
     * 0：已关闭
     * 1：已启用
     */
    private Integer status;

    private Date gmtBegin;

    private Date gmtEnd;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 规则模板
     */
    private String template;

    /**
     * if子规则才有
     */
    private String parentUuid;

    /**
     * 是否if rule
     */
    private boolean ifRule;

    /**
     * 规则类型
     * ONLINE:线上规则
     * TRY:试运行规则
     */
    private String ruleType;

    /**
     * 扩展字段, kv结构
     */
    private String attribute;

    private String policyUuid;

    /**
     * 是否已删除
     */
    private boolean deleted;



    /**
     * 策略定义
     */
    private String policyDefinitionUuid;



    /**
     * 业务uuid
     */
    private String bizUuid;

    /**
     * 业务类型
     * decision_flow:决策流
     * sub_policy:子策略
     *
     */
    private String bizType;

    /**
     * 风险配置
     {
     "mode":"WorstMatch"
     "riskDecision":"Accept"
     }
     或者
     {
     "mode":"Weighted",
     "riskWeight":10,
     "weightRatio":20.33,
     "op":"+",
     "property":{
     "type":"indicatrix/field",
     "name":"指标/字段"
     },
     "propertyValue":{
     "value":"3333333333"
     },
     "upperLimitScore":-30,
     "lowerLimitScore":30
     }
     */
    private String                       mode;                                    //WorstMatch,Weighted
    private String                       riskDecision;                            // 风险决策结果

    private double                       baseWeight;                              // 风险权重基线
    private double                       weightRatio;                             // 风险权重比例
    private String                       weightIndex;                             // 风险权重指标　
    private String                       indexType;                               // 指标类型    旧的为空,新的GAEA_INDICATRIX　
    private Double                       downLimitScore;                          // 权重计算分数右值下限
    private Double                       upLimitScore;                            // 权重计算分数右值上限


    private List<RuleConditionElementDTO> ruleConditionElements;
    private List<RuleActionElementDTO>    ruleActionElements;


    public Double getDownLimitScore() {
        if( downLimitScore == null){
            return -30d;
        }
        return downLimitScore;
    }

    public Double getUpLimitScore() {
        if(upLimitScore == null){
            return 30d;
        }
        return upLimitScore;
    }
}
