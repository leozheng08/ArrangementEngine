package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:19 下午
 */
public interface MobileInfoServiceExtPt extends IExtensionPoint {

    PhoneAttrEntity getMobileInfo(String phone, AbstractFraudContext context);
}
