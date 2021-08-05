package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyScriptConfigDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PolicyScriptConfigDAO {

    List<PolicyScriptConfigDO> selectAll();

    List<PolicyScriptConfigDO> selectByPolicyUuid(@Param("policyUuid") String policyUuid);

}