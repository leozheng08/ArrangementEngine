package cn.tongdun.kunpeng.api.engine.dto.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: liang.chen
 * @Date: 2020/1/17 下午10:15
 */
@Data
public class DecisionFlowModelDTO extends CommonDTO{

    private Long id;
    private Date gmtCreate;
    private Date gmtModify;

    /**
     * 关联holmes_model表的uuid model_uuid
     */
    private String modelUuid;

    /**
     * 合作方 partner_code
     */
    private String partnerCode;

    /**
     * 关联decision_flow表的uuid decision_flow_uuid
     */
    private String decisionFlowUuid;

    /**
     * 输入
     * [{"field":"accountMobile","rightFieldType":"field","rightField":"accountMobile","rightFieldName":""},
     * {"field":"accountName","rightFieldType":"field","rightField":"accountName","rightFieldName":""},
     * {"field":"idNumber","rightFieldType":"field","rightField":"idNumber","rightFieldName":""}]
     */
    private String inputs;

    /**
     * 输出变量，其中isRiskServiceOutput代表是否决策接口直接输出。如：[{ "field": "credit_score", "rightField":
     * "anchorScore", "rightFieldDisplayName": "主播模型分数", "isRiskServiceOutput": true }]
     */
    private String outputs;

    private boolean riskServiceOutput;
}
