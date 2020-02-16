package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;

import java.util.List;
import java.util.Set;

public interface PolicyDOMapper {


    PolicyDO selectByUuid(String uuid);

    List<PolicyDO> selectByPolicyDefinitionUuid(String policyDefinitionUuid);

    //根据合作列表，取得运行版本的策略清单
    List<PolicyModifiedDTO> selectDefaultPolicyByPartners(Set<String> partners);


    //根据策略uuid列表，取得策略清单
    List<PolicyModifiedDTO> selectPolicyByUuids(List<String> uuids);

}