package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.client.dto.RuleDTO;

/**
 * @description: 批量数据组装
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:44
 */
public interface BatchRemoteCallDataBuilder {
    /**
     * 从RuleDTO中获取需要批量处理的相关数据，写入Rule和对应的缓存当中
     * @param dto
     * @param rule
     * @return
     */
    Rule appendBatchRemoteCallData(RuleDTO dto,Rule rule);
}
