package cn.tongdun.kunpeng.api.engine.model.policy.challenger;


import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2020/3/10 下午5:49
 */
public interface IPolicyChallengerRepository {

    //根据合作方取得所有有效的挑战者任务
    List<PolicyChallenger> queryAvailableByPartners(Set<String> partners);

    //根据PolicyDefinitionUuid查询挑战者
    PolicyChallenger queryByPolicyDefinitionUuid(String policyDefinitionUuid);

    //根据uuid查询挑战者
    PolicyChallenger queryByUuid(String uuid);
}
