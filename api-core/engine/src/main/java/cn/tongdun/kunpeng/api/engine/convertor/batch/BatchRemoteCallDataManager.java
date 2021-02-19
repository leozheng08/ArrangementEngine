package cn.tongdun.kunpeng.api.engine.convertor.batch;

import cn.tongdun.kunpeng.client.dto.RuleDTO;

import java.util.List;

/**
 * @description: 组装远程批量调用数据
 * @author: zhongxiang.wang
 * @date: 2021-02-18 13:52
 */
public class BatchRemoteCallDataManager {

    /**
     * 组装数据
     * @param policyUuid
     * @param subPolicyUuid
     * @param dto
     * @return List<AbstractBatchRemoteCallData>
     * @see AbstractBatchRemoteCallData
     */
    public static List<Object> buildData(String policyUuid,String subPolicyUuid,RuleDTO dto){
        BatchRemoteCallDataBuilder builder = BatchRemoteCallDataBuilderFactory.getBuilder(dto.getTemplate());
        return null == builder ? null : builder.build(policyUuid,subPolicyUuid,dto);
    }
}
