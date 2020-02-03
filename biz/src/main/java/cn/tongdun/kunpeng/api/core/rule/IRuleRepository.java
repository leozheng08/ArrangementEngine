package cn.tongdun.kunpeng.api.core.rule;

import cn.tongdun.kunpeng.api.core.eventtype.EventType;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IRuleRepository {



    List<EventType> queryAll();
}
