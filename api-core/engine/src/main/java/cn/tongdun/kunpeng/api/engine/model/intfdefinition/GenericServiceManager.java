package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

/**
 * 泛化调用GenericService管理器
 */
public interface GenericServiceManager {

    /**
     * 获取泛化调用ReferenceConfig
     *
     * @param interfaceDefinition 接口信息
     * @return ReferenceConfig
     */
    ReferenceConfig<GenericService> getGenericServiceReferenceConfig(InterfaceDefinition interfaceDefinition);

    /**
     * 获取GenericService
     *
     * @param interfaceDefinition 接口信息
     * @return GenericService
     */
    GenericService getGenericService(InterfaceDefinition interfaceDefinition);

}