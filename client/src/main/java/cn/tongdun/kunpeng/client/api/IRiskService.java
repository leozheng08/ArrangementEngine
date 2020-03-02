package cn.tongdun.kunpeng.client.api;

import cn.tongdun.kunpeng.client.data.IRiskResponse;

import java.util.Map;

/**
 * Created by lvyadong on 2020/02/18.
 */
public interface IRiskService {

    IRiskResponse riskService(Map<String, String> request);
}
