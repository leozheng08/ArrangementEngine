package cn.tongdun.kunpeng.api.dataobject;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.List;

/**
 * 规则
 * 
 * @author du 2014年2月15日 下午5:45:09
 */
@Data
public class RuleDO extends CommonDO {

    private static final long            serialVersionUID = -2526030344227360438L;

    private String                       name;                                    // 规则名称
    private Timestamp                    gmtBegin;                                // 生效时间
    private Timestamp                    gmtEnd;                                  // 结束时间
    private Integer                      priority;                                // 优先级 int(3)

    private Integer                      displayOrder;                            // 显示顺序
    private List<RuleConditionElementDO> ruleConditionElements;
    private List<RuleActionElementDO>    ruleActionElements;
    private String                       causeCode;                               // 原因码
    private String                       operateCode;                             // 操作码
    private Integer                      valid;                                   // 是否启用
    private String                       fkPolicyUuid;                            // 策略uuid
    private String                       template;                                // 模板类型
    private String                       parentUuid;                              // 规则中的父节点即if条件
    private String                       ifClause;                                // if标识
    private List<RuleDO>                 children;                                // if下边的规则

    private double                       baseWeight;                              // 风险权重基线
    private double                       weightRatio;                             // 风险权重比例
    private String                       weightIndex;                             // 风险权重指标　
    private String                       indexType;                               // 指标类型    旧的为空,新的GAEA_INDICATRIX　
    private Double                       downLimitScore;                          // 权重计算分数右值下限
    private Double                       upLimitScore;                            // 权重计算分数右值上限


    /**
     * 重要变更，自定义规则id
     */
    private String ruleCustomId;


    /**
     * 规则命中时需要执行的动作。
     *
     */
    private String                       triggers;


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
