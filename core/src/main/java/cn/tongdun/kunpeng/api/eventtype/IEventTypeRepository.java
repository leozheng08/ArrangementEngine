package cn.tongdun.kunpeng.api.eventtype;

import cn.tongdun.kunpeng.api.field.RuleField;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IEventTypeRepository {

    public static final String EVENT_TYPE_ALL = "All";

    List<EventType> queryAll();
}
