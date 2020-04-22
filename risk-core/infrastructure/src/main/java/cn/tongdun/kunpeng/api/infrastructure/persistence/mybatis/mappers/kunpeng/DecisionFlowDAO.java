package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;

public interface DecisionFlowDAO {

    DecisionFlowDO selectByUuid(String uuid);

}