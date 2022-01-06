package cn.tongdun.kunpeng.api.engine.convertor.batch.mailmodel;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;

/**
 * @author: mengtao
 * @create: 2021-12-21 19:50
 */

public class MailModelBatchRemoteCallDataBuilder implements BatchRemoteCallDataBuilder {

    @Override
    public AbstractBatchRemoteCallData build(String policyUuid, String subPolicyUuid, String ruleUuid, RuleConditionElementDTO elementDTO) {
        return null;
    }
}
