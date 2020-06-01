package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.object.IdInfo;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:33 下午
 */
public interface IdInfoService extends IExtensionPoint {

    IdInfo getIdInfo(String id);
}
