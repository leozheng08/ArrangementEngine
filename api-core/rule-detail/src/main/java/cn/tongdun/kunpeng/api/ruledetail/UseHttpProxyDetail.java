package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

/**
 * http代理识别
 * @Author: liang.chen
 * @Date: 2020/2/6 下午7:54
 */
@Data
public class UseHttpProxyDetail extends ConditionDetail{

    private String proxyType;

    private String proxyStr;
    private String proxyInfo;

    public UseHttpProxyDetail() {
        super("use_http_proxy");
    }
}
