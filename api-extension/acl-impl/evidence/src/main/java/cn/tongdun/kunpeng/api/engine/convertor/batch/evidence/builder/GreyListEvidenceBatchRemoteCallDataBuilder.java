package cn.tongdun.kunpeng.api.engine.convertor.batch.evidence.builder;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;

/**
 * 欺诈灰名单
 * @Auther qingran.chen
 * @Date 2022/1/20
 */
public class GreyListEvidenceBatchRemoteCallDataBuilder implements BatchRemoteCallDataBuilder {
    @Override
    public AbstractBatchRemoteCallData build(String policyUuid, String subPolicyUuid, String ruleUuid, RuleConditionElementDTO elementDTO) {
        return null;
    }
}
