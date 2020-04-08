package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.SubPolicyDO;

import java.util.List;

public interface SubPolicyDAO {

    SubPolicyDO selectByUuid(String uuid);

    /**
     * 查询策略下面未删除的子策略
     *
     * @param policyUuid
     * @return
     */
    List<SubPolicyDO> selectListByPolicyUuid(String policyUuid);

}