package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/1/17 下午10:20
 */
@Data
public class PolicyIndicatrixItemDTO extends CommonDTO {
    /**
     * 合作方 partner_code
     */
    private String partnerCode;

    /**
     * 指标id indicatrix_id
     */
    private String indicatrixId;

    /**
     * 主属性字段 master_field
     */
    private String masterField;

    /**
     * 指标所在策略uuid policy_uuid
     */
    private String policyUuid;

    /**
     * 指标被引用业务的uuid biz_uuid
     */
    private String bizUuid;

    /**
     * 类型
     * RULE：规则
     * CUSTOM_OUTPUT:决策结果自定义
     * DECISION_FLOW：决策流
     */
    private String bizType;

    /**
     * 业务明细uuid
     * 比如
     * 情况1、：bizType=decision_flow，那么bizUuid=决策流uuid，bizItemUuid=规则uuid
     * 情况2、：bizType=rule，那么bizUuid=规则uuid，bizItemUuid=null
     *
     */
    private String bizItemUuid;

    /**
     * 子类型
     */
    private String bizItemType;

    /**
     * 状态 0：无效 1：有效 status
     */
    private Integer status;
}
