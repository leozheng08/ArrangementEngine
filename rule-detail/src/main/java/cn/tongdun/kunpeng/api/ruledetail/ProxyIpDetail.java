package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;


/**
 * 判断IP是否为代理访问
 * @Author: liang.chen
 * @Date: 2020/2/6 下午9:53
 */
@Data
public class ProxyIpDetail extends ConditionDetail {

    private String proxyIpType;

    public ProxyIpDetail(){
        super("proxy_ip");
    }

}
