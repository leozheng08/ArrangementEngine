package cn.tongdun.kunpeng.client.api;

import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;

import java.util.Map;

/**
 * Created by lvyadong on 2020/02/18.
 */
public interface IRiskService {

    IRiskResponse riskService(Map<String, Object> request);

    IRiskResponse riskService(RiskRequest request);

    IRiskResponse riskService(Map<String, Object> request, String bizName);

    IRiskResponse riskService(RiskRequest request, String bizName);
}
