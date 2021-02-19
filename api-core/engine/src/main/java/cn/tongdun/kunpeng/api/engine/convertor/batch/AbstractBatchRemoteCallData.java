package cn.tongdun.kunpeng.api.engine.convertor.batch;

import lombok.Data;

/**
 * @description: 批量远程调用数据
 * @author: zhongxiang.wang
 * @date: 2021-01-28 18:14
 */
@Data
public class AbstractBatchRemoteCallData {
    /**
     * 策略类型
     * @see cn.tongdun.kunpeng.api.common.Constant.Function
     */
    private String template;

    /**
     * 策略uuid
     */
    private String policyUuid;

    /**
     * 子策略uuid
     */
    private String subPolicyUuid;

    /**
     * 规则id
     */
    private String ruleUuid;
}
