package cn.tongdun.kunpeng.api.engine.model.Indicatrix;

import cn.tongdun.kunpeng.api.engine.dto.PolicyIndicatrixItemDTO;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/10 下午5:11
 */
public interface IPolicyIndicatrixItemRepository2 {

    /**
     * 根据策略查询策略指标定义
     */
    List<PolicyIndicatrixItemDTO> queryByPolicyUuid(String policyUuid);


    /**
     * 根据uuid查询策略指标定义
     */
    PolicyIndicatrixItemDTO queryByUuid(String uuid);
}
