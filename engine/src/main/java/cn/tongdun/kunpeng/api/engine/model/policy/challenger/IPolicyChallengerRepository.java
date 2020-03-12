package cn.tongdun.kunpeng.api.engine.model.policy.challenger;

import cn.tongdun.kunpeng.api.engine.dto.PolicyChallengerDTO;

/**
 * @Author: liang.chen
 * @Date: 2020/3/10 下午5:49
 */
public interface IPolicyChallengerRepository {

    //根据PolicyDefinitionUuid查询挑战者
    PolicyChallengerDTO queryByPolicyDefinitionUuid(String policyDefinitionUuid);

    //根据uuid查询挑战者
    PolicyChallengerDTO queryByUuid(String uuid);
}
