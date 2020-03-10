package cn.tongdun.kunpeng.api.engine.model.policy;

import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPolicyRepository {


    //根据合作列表，取得运行版本的策略清单
    List<PolicyModifiedDTO> queryDefaultPolicyByPartners(Set<String> partners);



    //根据合作列表，取得挑战者版本的策略清单
    List<PolicyModifiedDTO> queryChallengerPolicyByPartners(Set<String> partners);


    //查询单个策略的完整信息，包含各个子对象
    PolicyDTO queryByUuid(String uuid);
}
