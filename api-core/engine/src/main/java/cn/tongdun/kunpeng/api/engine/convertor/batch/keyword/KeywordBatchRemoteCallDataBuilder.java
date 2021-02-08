package cn.tongdun.kunpeng.api.engine.convertor.batch.keyword;

import cn.hutool.core.map.MapUtil;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.api.engine.dto.RuleParamDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.util.RuleParamUtil;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.share.json.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 关键词规则 批量数据组装器
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:37
 */
@Component
public class KeywordBatchRemoteCallDataBuilder implements BatchRemoteCallDataBuilder {

    @Autowired
    private BatchRemoteCallDataCache cache;

    /**
     * 从规则中解析出策略中涉及批量远程调用的数据，存入缓存和规则中
     * @param dto
     * @param rule
     * @return
     */
    @Override
    public Rule appendBatchRemoteCallData(RuleDTO dto, Rule rule) {
        String policyUuid = dto.getPolicyUuid();
        List<RuleConditionElementDTO> elements = dto.getRuleConditionElements();
        if(CollectionUtils.isEmpty(elements)){
            return rule;
        }
        List<Object> batchDataDTOS = new ArrayList<>();
        for(RuleConditionElementDTO elementDTO : elements){
            batchDataDTOS.add(this.createRemoteCallData(elementDTO));
        }

        //TODO rule中是否需要保存
        rule.setBatchRemoteCallData(MapUtil.of(policyUuid, MapUtil.of(Constant.Function.KEYWORD_WORDLIST,batchDataDTOS)));
        cache.put(policyUuid,MapUtil.of(Constant.Function.KEYWORD_WORDLIST,batchDataDTOS));
        return rule;
    }

    /**
     * 从param中解析出数据，转化为BatchDataDTO
     * keyword中无嵌套规则，如果是自定义规则，可能出现嵌套
     * @param elementDTO 中params格式：[{"name":"calcField","type":"string","value":"partnerCode"},{"name":"definitionList","type":"string","value":"sdgjcb"}]
     * @return
     */
    public KeywordBatchRemoteCallData createRemoteCallData(RuleConditionElementDTO elementDTO){
        String params = elementDTO.getParams();
        List<RuleParamDTO> all = JSON.parseArray(params, RuleParamDTO.class);
        KeywordBatchRemoteCallData batchDataDTO = new KeywordBatchRemoteCallData();
        batchDataDTO.setTemplate(Constant.Function.KEYWORD_WORDLIST);
        batchDataDTO.setCalcField(RuleParamUtil.getValue(all, "calcField"));
        batchDataDTO.setDefinitionList(RuleParamUtil.getValue(all, "definitionList"));
        return batchDataDTO;
    }
}
