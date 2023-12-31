package cn.tongdun.kunpeng.api.engine.dto;


import cn.tongdun.kunpeng.client.dto.CommonDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import lombok.Data;

import java.util.*;

/**
 * @author jian.li
 */
@Data
public class SubPolicyDTO extends CommonDTO {

    private static final long            serialVersionUID         = 1L;

    private String name;

    /**
     * 策略模式
     * FirstMatch:最先匹配
     * WorstMatch：最坏匹配
     * Weighted：权重
     */
    private String mode;

    private String description;

    /**
     * 版本，目前没有用
     */
    private String version;

    private String partnerCode;

    /**
     * 是否模板
     */
    private boolean template;

    /**
     * 风险类型
     */
    private String riskType;

    /**
     * 优先级
     * 值越小优先级最高
     * 1>2
     */
    private Integer priority;

    /**
     * 扩展字段，json结构
     *
     * mode=Weighted 才有下面的风险阈值配置
     * {
     * "riskThreshold":[
     *     {
     *         "start":0,
     *         "end":10,
     *         "riskDecision":"Accept"
     *     },
     *     {
     *         "start":10,
     *         "end":50,
     *         "riskDecision":"Review"
     *     },
     *     {
     *         "start":50,
     *         "end":100,
     *         "riskDecision":"Reject"
     *     }
     * ]
     * }
     */
    private String attribute;

    private String policyDefinitionUuid;

    private String policyUuid;



    private Date gmtDelete;

    private List<RuleDTO> rules;
    //按执行顺序放
    private List<String> ruleUuidList;

    private List<IndexDefinitionDTO> indexDefinitionList;

}
