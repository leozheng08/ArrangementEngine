package cn.tongdun.kunpeng.api.engine.convertor.batch.evidence;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.api.engine.dto.RuleParamDTO;
import cn.tongdun.kunpeng.api.engine.util.RuleParamUtil;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.share.json.JSON;

import java.util.List;

public class EvidenceBatchRemoteCallDataBuilder implements BatchRemoteCallDataBuilder {
    @Override
    public AbstractBatchRemoteCallData build(String policyUuid, String subPolicyUuid, String ruleUuid, RuleConditionElementDTO elementDTO) {
        EvidenceBatchRemoteCallData batchRemoteCallData = this.createRemoteCallData(policyUuid, subPolicyUuid, ruleUuid, elementDTO);
        return batchRemoteCallData;
    }

    /**
     * 从param中解析出数据，转化为BatchDataDTO
     * keyword中无嵌套规则，如果是自定义规则，可能出现嵌套
     *
     * @param policyUuid
     * @param subPolicyUuid
     * @param ruleUuid      规则uuid
     * @param elementDTO    中params格式：[{"name":"timeslice","type":"int","value":"0"},{"name":"timeunit","type":"string","value":"y"},{"name":"unlimitedTime","type":"string","value":"true"},{"name":"calcField","type":"string","value":"partnerCode"},{"name":"evidenceType","type":"string","value":"accountMobileHash"},{"name":"minimumSimilarity","type":"int","value":"90"},{"name":"fraudtype","type":"string","value":"creditCrack"},{"name":"scope","type":"enum","value":"Scope.APPLICATION"},{"name":"iterateType","type":"string","value":"any"}]
     * @return
     */
    public EvidenceBatchRemoteCallData createRemoteCallData(String policyUuid, String subPolicyUuid, String ruleUuid, RuleConditionElementDTO elementDTO) {
        String params = elementDTO.getParams();
        List<RuleParamDTO> all = JSON.parseArray(params, RuleParamDTO.class);
        EvidenceBatchRemoteCallData batchDataDTO = new EvidenceBatchRemoteCallData();
        batchDataDTO.setTemplate(Constant.Function.EVIDENCE_EVIDENCE);
        batchDataDTO.setPolicyUuid(policyUuid);
        batchDataDTO.setSubPolicyUuid(subPolicyUuid);
        batchDataDTO.setRuleUuid(ruleUuid);
        batchDataDTO.setCalcField(RuleParamUtil.getValue(all, "calcField"));
        batchDataDTO.setEvidenceType(RuleParamUtil.getValue(all, "evidenceType"));
        batchDataDTO.setFraudtype(RuleParamUtil.getValue(all, "fraudtype"));
        batchDataDTO.setIterateType(RuleParamUtil.getValue(all, "iterateType"));
        batchDataDTO.setScope(RuleParamUtil.getValue(all, "scope"));
        batchDataDTO.setMinimumSimilarity(RuleParamUtil.getValue(all, "minimumSimilarity"));
        batchDataDTO.setTimeslice(RuleParamUtil.getValue(all, "timeslice"));
        batchDataDTO.setTimeunit(RuleParamUtil.getValue(all, "timeunit"));
        batchDataDTO.setUnlimitedTime(RuleParamUtil.getValue(all, "unlimitedTime"));
        return batchDataDTO;
    }
}
