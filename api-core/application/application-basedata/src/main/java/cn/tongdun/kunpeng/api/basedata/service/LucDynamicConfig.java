package cn.tongdun.kunpeng.api.basedata.service;

/**
 * 用于LUC动态配置的接口，已默认实现，若有不同，需在业务层实现该接口
 *
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