package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

/**
 * vpn
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
