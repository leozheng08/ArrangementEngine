package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

/**
 * IOS http代理识别
 * @Author: liang.chen
 * @Date: 2020/2/6 下午7:54
 */
@Data
public class IOSUseHttpProxyDetail extends UseHttpProxyDetail{

    private String proxyType;

    public IOSUseHttpProxyDetail() {
        super();
    }
}
