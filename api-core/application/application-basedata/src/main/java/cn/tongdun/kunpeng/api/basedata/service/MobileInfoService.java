package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:19 下午
 */
public interface MobileInfoService extends IExtensionPoint {

    MobileInfoDO getMobileInfo(String phone);
}
