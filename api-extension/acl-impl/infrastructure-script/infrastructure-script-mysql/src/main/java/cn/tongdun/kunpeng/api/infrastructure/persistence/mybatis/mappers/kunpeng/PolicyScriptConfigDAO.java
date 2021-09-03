package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyScriptConfigDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liupei
 * @Date 2021-08-30
 */
public interface PolicyScriptConfigDAO {

    List<PolicyScriptConfigDO> selectAll();

    List<PolicyScriptConfigDO> selectEnabledByPolicyUuid(@Param("policyUuid") String policyUuid);

}