package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.common.data.AbstractFraudContext;

public interface IGenericDubboCaller {

    boolean call(AbstractFraudContext fraudContext, // 你懂的
                 InterfaceDefinitionInfo interfaceInfo, //接口配置信息
                 String indexUuids, // 指标uuid拼接
                 String fields // 字段拼接
    );
}
