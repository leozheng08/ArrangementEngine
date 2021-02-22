package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.client.dto.RuleDTO;

import java.util.List;

/**
 * @description: 批量数据组装
 * @author: zhongxiang.wang
 * @date: 2021-01-28 14:44
 */
public interface BatchRemoteCallDataBuilder {
    /**
     * 从RuleDTO中获取需要批量处理的相关数据进行组装
     *
     * @param policyUuid
     * @param subPolicyUuid
     * @param dto
     * @return
     */
    List<Object> build(String policyUuid, String subPolicyUuid, RuleDTO dto);
}
