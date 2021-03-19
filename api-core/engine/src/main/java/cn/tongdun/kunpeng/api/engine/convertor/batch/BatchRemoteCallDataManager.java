package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @description: 组装远程批量调用数据
 * @author: zhongxiang.wang
 * @date: 2021-02-18 13:52
 */
public class BatchRemoteCallDataManager {

    /**
     * 组装数据
     *
     * 分2种情况：
     * 1.规则的模版类型是支持批量的
     * 这种情况template只有一种
     *
     * 2.规则的模版是自定义类型的，自定义规则的条件里，有嵌套支持批量的规则模版
     * 这种情况template可能有多种
     * 自定义里只能嵌套一层，不能嵌套多层，所以这里不涉及递归
     *
     * @param policyUuid
     * @param subPolicyUuid
     * @param ruleDTO
     * @return Map<String, List < Object>>  key:template
     * @see AbstractBatchRemoteCallData
     */
    public static Map<String, List<Object>> buildData(String policyUuid, String subPolicyUuid, RuleDTO ruleDTO) {
        if (StringUtils.isEmpty(policyUuid) || StringUtils.isEmpty(subPolicyUuid)) {
            return null;
        }
        List<RuleConditionElementDTO> elements = ruleDTO.getRuleConditionElements();
        if (CollectionUtils.isEmpty(elements)) {
            return null;
        }
        Map<String, List<Object>> batchDatas = new HashMap<>();
        if (BatchRemoteCallDataBuilderFactory.supportBatchRemoteCall(ruleDTO.getTemplate())) {
            List<Object> datas = new ArrayList<>();
            String template = ruleDTO.getTemplate();
            for (RuleConditionElementDTO elementDTO : elements) {
                BatchRemoteCallDataBuilder builder = BatchRemoteCallDataBuilderFactory.getBuilder(template);
                datas.add(builder.build(policyUuid, subPolicyUuid, ruleDTO.getUuid(), elementDTO));
            }
            batchDatas.put(template, datas);
        } else if (BatchRemoteCallDataBuilderFactory.supportBatchRemoteCallInCustom(ruleDTO)) {
            for (RuleConditionElementDTO elementDTO : elements) {
                List<RuleConditionElementDTO> subConditions = elementDTO.getSubConditions();
                if (!CollectionUtils.isEmpty(subConditions)) {
                    //规则里条件有子条件
                    for (RuleConditionElementDTO subDto : subConditions) {
                        String template = subDto.getLeftProperty();
                        if (BatchRemoteCallDataBuilderFactory.supportBatchRemoteCall(template)) {
                            BatchRemoteCallDataBuilder builder = BatchRemoteCallDataBuilderFactory.getBuilder(template);
                            Object data = builder.build(policyUuid, subPolicyUuid, ruleDTO.getUuid(), subDto);
                            List<Object> dataByTemplate = batchDatas.get(template);
                            if (CollectionUtils.isEmpty(dataByTemplate)) {
                                List<Object> objects = new ArrayList<>();
                                objects.add(data);
                                batchDatas.put(template, objects);
                            } else {
                                //一个自定义规则下，嵌套多个其他规则模版，多个规则模版如果有重复？这里不做去重，理由如下：
                                //1.由于AbstractBatchRemoteCallData的子类里，属性不尽相同，这里无法直接给出通用比较
                                //2.自定义规则+嵌套多个支持批量操作的规则模版+这几个支持批量的规则模版里有完全相同的配置，这三个条件限制后，即使是认为配置错，几率也是极低的，这里可以不考虑
                                //3.即使2情况发生了，无负面影响，删除修改和关闭等操作，这一点重复的缓存都会被清理掉，无负面影响
                                dataByTemplate.add(data);
                                batchDatas.put(template, dataByTemplate);
                            }
                        }
                    }
                }
            }
        }
        return batchDatas;
    }
}
