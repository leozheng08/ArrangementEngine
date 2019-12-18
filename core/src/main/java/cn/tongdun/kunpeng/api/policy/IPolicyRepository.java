package cn.tongdun.kunpeng.api.policy;

import cn.tongdun.kunpeng.api.dataobject.PolicyDO;
import cn.tongdun.kunpeng.api.dataobject.PolicyModifiedDO;
import cn.tongdun.kunpeng.api.eventtype.EventType;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPolicyRepository {


    //取得所有策略清单
    List<PolicyModifiedDO> queryByPartners(Set<String> partners);


    PolicyDO queryByUuid(String uuid);
}
