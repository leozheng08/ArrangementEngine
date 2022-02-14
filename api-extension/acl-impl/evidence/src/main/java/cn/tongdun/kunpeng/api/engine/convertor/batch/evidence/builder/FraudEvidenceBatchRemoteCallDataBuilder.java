package cn.tongdun.kunpeng.api.engine.convertor.batch.evidence.builder;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;

/**
 * 欺诈证据库
 */
public class FraudEvidenceBatchRemoteCallDataBuilder implements BatchRemoteCallDataBuilder {

    @Override
    public AbstractBatchRemoteCallData build(String policyUuid, String subPolicyUuid, String ruleUuid, RuleConditionElementDTO elementDTO) {
        return null;
    }
}
