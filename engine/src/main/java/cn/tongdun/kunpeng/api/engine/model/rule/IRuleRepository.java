package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IRuleRepository {


    RuleDTO queryByUuid(String ruleUuid);
}
