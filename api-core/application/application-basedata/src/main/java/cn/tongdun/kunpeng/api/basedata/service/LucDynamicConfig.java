package cn.tongdun.kunpeng.api.basedata.service;

/**
 * @author : tingchang.fan
 * @projectName：kunpeng-api
 * @date 2022/9/8 2:22 下午
 * @Copyright: 版权所有 (C) 2022 同盾科技.
 */
public interface LucDynamicConfig {

    default String getCardBinInfoDubboSwitch() {
        return "0";
    }
}