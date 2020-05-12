package cn.tongdun.kunpeng.api.engine.load.bypartner;

import cn.tongdun.tdframework.core.pipeline.IStep;

/**
 * 只加载单个合作方的信息，供集群迁移时调用
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface ILoadByPartner extends IStep {
    boolean loadByPartner(String partnerCode);
}
