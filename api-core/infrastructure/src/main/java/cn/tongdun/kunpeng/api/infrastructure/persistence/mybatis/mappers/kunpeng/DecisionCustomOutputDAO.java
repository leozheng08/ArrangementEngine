package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionCustomOutputDO;

import java.util.List;
import java.util.Set;

public interface DecisionCustomOutputDAO {

    /**
     * 查询未删除的
     *
     * @param policyUuid
     * @return
     */
    List<DecisionCustomOutputDO> selectAvailableByPolicyUuid(String policyUuid);

    DecisionCustomOutputDO selectByUuid(String uuid);

    List<DecisionCustomOutputDO> selectAvailableByPartners(Set<String> partners);

}