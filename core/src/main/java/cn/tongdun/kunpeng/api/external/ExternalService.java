package cn.tongdun.kunpeng.api.external;

import cn.tongdun.ddd.domain.Entity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午5:39
 */
@Data
public class ExternalService extends Entity {

    private String uuid;

    //接口名称（中文名）
    String name;

    //接口类名
    String serviceName;

    //方法名
    String methodName;

    //版本号
    String version;

    //超时时间，毫秒
    int timeout;

    //重试次数
    int retryCount;

    //输入参数
    String inputParams;

    //输出参数
    String outputParams;

}
