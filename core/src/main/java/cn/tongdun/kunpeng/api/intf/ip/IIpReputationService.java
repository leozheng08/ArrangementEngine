package cn.tongdun.kunpeng.api.intf.ip;

import cn.tongdun.kunpeng.api.intf.ip.entity.IpReputationObj;
import cn.tongdun.kunpeng.api.intf.ip.entity.IpReputationRulesObj;

/**
 * @Author: liang.chen
 * @Date: 2020/1/7 下午4:54
 */
public interface IIpReputationService {

    IpReputationObj getIpInfos(String var1, boolean var2);

    IpReputationRulesObj getIpInfosRulesObj2(String var1);
}
