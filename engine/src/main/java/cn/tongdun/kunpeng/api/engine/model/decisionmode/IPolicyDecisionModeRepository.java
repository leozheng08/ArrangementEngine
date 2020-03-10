package cn.tongdun.kunpeng.api.engine.model.policy;

import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDecisionModeDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPolicyRepository {


    /**
     * 查询策略当前在用的运行模式
     * @param policyUuid
     * @return
     */
    PolicyDecisionModeDTO queryByPolicyUuid(String policyUuid);

    /**
     * 根据uuid查询运行模式
     * @param uuid
     * @return
     */
    PolicyDecisionModeDTO queryByUuid(String uuid);
}
