package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.convertor.batch.keyword.KeywordBatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @description: 根据策略模版获取对应的批量数据处理对象
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:44
 */
@Slf4j
public class BatchRemoteCallDataBuilderFactory {

    /**
     * key:{@link Constant.Function} 规则模版类型
     * value: BatchRemoteCallDataBuilder
     */
    private static final Map<String, BatchRemoteCallDataBuilder> builders = new HashMap<>(4);

    static {
        builders.put(Constant.Function.KEYWORD_WORDLIST, new KeywordBatchRemoteCallDataBuilder());
    }

    public static BatchRemoteCallDataBuilder getBuilder(String template) {
        if (StringUtils.isBlank(template)) {
            log.error(TraceUtils.getTrace() + "规则模版template参数为空，请确认数据是否正确。");
        }
        BatchRemoteCallDataBuilder batchRemoteCallDataBuilder = builders.get(template);
        if (null == batchRemoteCallDataBuilder) {
            throw new RuntimeException("未查找到template=" + template + "的BatchRemoteCallDataBuilder，请确认是否添加初始化");
        }
        return batchRemoteCallDataBuilder;
    }

    /**
     * 是否支持批量远程调用
     * @param template 规则模版类型
     * @return
     */
    public static boolean supportBatchRemoteCall(String template){
        if(CollectionUtils.isEmpty(builders) || StringUtils.isEmpty(template)){
            return false;
        }
        return builders.keySet().contains(template);
    }

    /**
     * 自定义规则下的条件里，是否有关键词等支持批量操作的规则模版
     * common/custom 自定义规则比较特殊，规则的条件可以嵌套其他规则模版（但仅能嵌套一层，不能多层）
     * 所以当规则为common/custom时，需要判断它的条件里是否有关键词等支持批量操作的规则模版
     *
     * @param ruleDO
     * @return
     */
    public static boolean supportBatchRemoteCallInCustom(RuleDTO ruleDO) {
        if (Constant.Function.COMNON_CUSTOM.equals(ruleDO.getTemplate())) {
            List<RuleConditionElementDTO> elements = ruleDO.getRuleConditionElements();
            if (!CollectionUtils.isEmpty(elements)) {
                for (RuleConditionElementDTO dto : elements) {
                    List<RuleConditionElementDTO> subConditions = dto.getSubConditions();
                    if (!CollectionUtils.isEmpty(subConditions)) {
                        for (RuleConditionElementDTO subDto : subConditions) {
                            //规则的条件里如果有规则模版，类型在leftProperty字段中
                            String template = subDto.getLeftProperty();
                            if (supportBatchRemoteCall(template)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
