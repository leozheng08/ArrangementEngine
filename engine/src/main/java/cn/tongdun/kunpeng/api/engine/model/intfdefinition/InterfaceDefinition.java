package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import lombok.Data;


@Data
public class InterfaceDefinition extends StatusEntity {

    /**
     * 所属应用，非合作方应用 application
     */
    private String application;

    /**
     * 名称 name
     */
    private String name;

    /**
     * 显示名称 display_name
     */
    private String displayName;

    /**
     * 服务全称 service_name
     */
    private String serviceName;

    /**
     * 方法 method_name
     */
    private String methodName;

    /**
     * 方法参数类型 method_parameter_type
     */
    private String methodParameterType;

    /**
     * 版本 version
     */
    private String version;

    /**
     * 超时时间，单位毫秒 timeout
     */
    private Integer timeout;

    /**
     * 重试次数 retry_count
     */
    private Integer retryCount;

    /**
     * 描述 description
     */
    private String description;


    /**
     * 接口唯一标识 interface_id
     */
    private String interfaceId;

    /**
     * 输入参数 input_param
     */
    private String inputParam;

    /**
     * 返回参数 output_param
     */
    private String outputParam;

    /**
     * 模板 template
     */
    private String template;


}