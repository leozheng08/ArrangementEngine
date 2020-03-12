package cn.tongdun.kunpeng.api.engine.model.policyindex;

import cn.tongdun.kunpeng.api.engine.dto.IndexDefinitionDTO;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/10 下午5:11
 */
public interface IPolicyIndexRepository {

    /**
     * 根据策略查询策略指标定义
     */
    List<IndexDefinitionDTO> queryByPolicyUuid(String policyUuid);


    /**
     * 根据子策略查询策略指标定义
     */
    List<IndexDefinitionDTO> queryBySubPolicyUuid(String subPolicyUuid);

    /**
     * 根据uuid查询策略指标定义
     */
    IndexDefinitionDTO queryByUuid(String uuid);
}
