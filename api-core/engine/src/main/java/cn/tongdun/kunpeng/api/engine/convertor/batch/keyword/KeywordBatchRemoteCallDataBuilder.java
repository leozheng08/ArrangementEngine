package cn.tongdun.kunpeng.api.engine.convertor.batch.keyword;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.api.engine.dto.RuleParamDTO;
import cn.tongdun.kunpeng.api.engine.util.RuleParamUtil;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.share.json.JSON;
import java.util.List;

/**
 * @description: 关键词规则 批量数据组装器
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:37
 */
public class KeywordBatchRemoteCallDataBuilder implements BatchRemoteCallDataBuilder {

    @Override
    public AbstractBatchRemoteCallData build(String policyUuid, String subPolicyUuid, String ruleUuid, RuleConditionElementDTO elementDTO) {
        KeywordBatchRemoteCallData remoteCallData = this.createRemoteCallData(policyUuid, subPolicyUuid, ruleUuid, elementDTO);
        return remoteCallData;
    }

    /**
     * 从param中解析出数据，转化为BatchDataDTO
     * keyword中无嵌套规则，如果是自定义规则，可能出现嵌套
     *
     * @param policyUuid
     * @param subPolicyUuid
     * @param ruleUuid      规则uuid
     * @param elementDTO    中params格式：[{"name":"calcField","type":"string","value":"partnerCode"},{"name":"definitionList","type":"string","value":"sdgjcb"}]
     * @return
     */
    public KeywordBatchRemoteCallData createRemoteCallData(String policyUuid, String subPolicyUuid, String ruleUuid, RuleConditionElementDTO elementDTO) {
        String params = elementDTO.getParams();
        List<RuleParamDTO> all = JSON.parseArray(params, RuleParamDTO.class);
        KeywordBatchRemoteCallData batchDataDTO = new KeywordBatchRemoteCallData();
        batchDataDTO.setTemplate(Constant.Function.KEYWORD_WORDLIST);
        batchDataDTO.setPolicyUuid(policyUuid);
        batchDataDTO.setSubPolicyUuid(subPolicyUuid);
        batchDataDTO.setRuleUuid(ruleUuid);
        batchDataDTO.setCalcField(RuleParamUtil.getValue(all, "calcField"));
        batchDataDTO.setDefinitionList(RuleParamUtil.getValue(all, "definitionList"));
        return batchDataDTO;
    }
}
