package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionCustomOutputItemDO;

import java.util.List;

public interface DecisionCustomOutputItemDAO {

    List<DecisionCustomOutputItemDO> selectByOutputUuid(String outputUuid);

}