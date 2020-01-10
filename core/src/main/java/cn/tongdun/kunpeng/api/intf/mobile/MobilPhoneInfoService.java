package cn.tongdun.kunpeng.api.intf.mobile;

import cn.tongdun.kunpeng.api.intf.mobile.entity.MobilPhoneInfosObj;
import cn.tongdun.kunpeng.api.intf.mobile.entity.MobilPhoneRuleObj;

/**
 * 项目: alliance
 * 作者: 潘清剑(qingjian.pan@tongdun.cn)
 * 时间: 2016/10/24 下午2:15
 * 描述:
 */
public interface MobilPhoneInfoService {

    /**
     * 获取手机信息(直接从缓存中拿)
     */
    MobilPhoneInfosObj getMobilPhoneInfos(String phoneNumber, boolean directCache) throws Exception;

    /**
     * 获取手机规则信息(可选择是否直接从缓存中取数据)
     */
    MobilPhoneRuleObj getMobilPhoneRule(String phoneNumber, boolean directCache) throws Exception;


}
