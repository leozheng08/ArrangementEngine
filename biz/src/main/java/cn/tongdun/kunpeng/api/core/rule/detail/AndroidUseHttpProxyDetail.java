package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

/**
 * Android http代理识别
 * @Author: liang.chen
 * @Date: 2020/2/6 下午7:54
 */
@Data
public class AndroidUseHttpProxyDetail extends UseHttpProxyDetail{

    private String proxyStr;
    private String proxyInfo;

    public AndroidUseHttpProxyDetail() {
        super();
    }
}
