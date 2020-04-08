package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DecisionCustomOutputFormulaDO;

import java.util.List;

public interface DecisionCustomOutputFormulaDAO {

    List<DecisionCustomOutputFormulaDO> selectByOutputUuid(String outputUuid);
}