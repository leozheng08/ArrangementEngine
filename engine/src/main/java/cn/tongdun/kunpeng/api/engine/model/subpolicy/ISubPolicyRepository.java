package cn.tongdun.kunpeng.api.engine.model.subpolicy;

import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface ISubPolicyRepository {

    //根据策略uuid查询子策略列表，包含策略的各个子对象的完整信息，
    public List<SubPolicyDTO> queryFullByPolicyUuid(String policyUuid);

    //查询单个策略的信息，包含各个子对象
    SubPolicyDTO queryFullByUuid(String uuid);

    //查询单个策略的信息
    SubPolicyDTO queryByUuid(String uuid);
}
