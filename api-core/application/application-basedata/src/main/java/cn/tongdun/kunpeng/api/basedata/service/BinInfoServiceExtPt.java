package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.object.BinInfoDO;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:41 下午
 */
public interface BinInfoServiceExtPt extends IExtensionPoint {

    BinInfoDO getBinInfo(String binCode);
}
