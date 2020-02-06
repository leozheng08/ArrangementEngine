package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/2/6 下午7:54
 */
@Data
public class UseVpnDetail extends ConditionDetail{

    private String vpnIp;

    public UseVpnDetail() {
        super("use_vpn");
    }
}
