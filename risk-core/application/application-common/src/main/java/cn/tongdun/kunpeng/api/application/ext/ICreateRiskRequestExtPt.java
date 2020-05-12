package cn.tongdun.kunpeng.api.application.ext;

import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;

import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 下午5:16
 */
public interface ICreateRiskRequestExtPt extends IExtensionPoint {

    RiskRequest createRiskRequest(Map<String, String> request);
}
