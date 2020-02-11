package cn.tongdun.kunpeng.api.engine.model.interf;

import cn.tongdun.ddd.common.domain.Entity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/1/17 下午10:10
 */
@Data
public class InterfaceDefinition extends Entity {
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
     * 输入参数 input_param
     */
    private String inputParam;

    /**
     * 返回参数 output_param
     */
    private String outputParam;

    /**
     * 描述 description
     */
    private String description;

    /**
     * 是否删除 is_deleted
     */
    private boolean deleted;

    /**
     * 模板 template
     */
    private String template;

    /**
     * 接口唯一标识 interface_id
     */
    private String interfaceId;

    /**
     * 状态 0:关闭 1:开启 status
     */
    private Integer status;
}
