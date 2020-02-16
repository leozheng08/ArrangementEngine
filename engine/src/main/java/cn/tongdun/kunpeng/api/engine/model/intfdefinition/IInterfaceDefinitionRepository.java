package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/2/16 上午3:48
 */
public interface IInterfaceDefinitionRepository {
    List<InterfaceDefinition> queryAllAvailable();
}
