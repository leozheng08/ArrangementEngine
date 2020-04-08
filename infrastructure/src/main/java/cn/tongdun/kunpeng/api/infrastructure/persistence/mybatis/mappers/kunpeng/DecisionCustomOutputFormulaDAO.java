package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionCustomOutputFormulaDO;

import java.util.List;

public interface DecisionCustomOutputFormulaDOMapper {

    List<DecisionCustomOutputFormulaDO> selectByOutputUuid(String outputUuid);
}