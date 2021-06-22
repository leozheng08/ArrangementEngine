package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Data
public class InterfaceDefinition extends StatusEntity {

    private static final Logger logger = LoggerFactory.getLogger(InterfaceDefinition.class);

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

    @Override
    public boolean isValid() {
        try {
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getInterfaceId()), "interfaceId can't be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getApplication()), "application can't be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getUuid()), "interface uuid can't be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getName()), "interface name can't be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getServiceName()), "service name can't be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getMethodName()), "method name can't be empty");
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getVersion()), "version can't be empty");
            Preconditions.checkArgument(this.getTimeout() >= 1, "timeout can't be less than 1ms");
            Preconditions.checkArgument(this.getRetryCount() >= 0, "retry count can't be less than 0");
            Preconditions.checkArgument(StringUtils.isNotEmpty(this.getInputParam()), "input parameters can't be empty");
        } catch (Exception e) {
            logger.error("验证返回接口参数有效性异常 service:{}", JSON.toJSONString(this), e);
            return false;
        }
        return true;
    }
}