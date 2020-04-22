package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.RuleActionElementDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleActionElement4ConsumerDAO {

    List<RuleActionElementDO> selectByRuleUuid(@Param("ruleUuid") String ruleUuid);

}
