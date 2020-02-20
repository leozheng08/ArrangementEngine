package cn.tongdun.kunpeng.client.api;

import cn.tongdun.kunpeng.client.data.RiskResponse;

import java.util.Map;

/**
 * Created by lvyadong on 2020/02/18.
 */
public interface IRiskService {

    RiskResponse riskService(Map<String, String> request);
}
