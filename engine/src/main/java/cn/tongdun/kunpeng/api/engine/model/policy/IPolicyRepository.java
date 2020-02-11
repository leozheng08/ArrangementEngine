package cn.tongdun.kunpeng.api.engine.model.policy;

import cn.tongdun.kunpeng.api.engine.dto.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.dto.PolicyModifiedDTO;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPolicyRepository {


    //取得所有策略清单
    List<PolicyModifiedDTO> queryByPartners(Set<String> partners);


    //取得所有策略清单
    PolicyModifiedDTO queryByPartner(String partner);


    PolicyDTO queryByUuid(String uuid);
}
