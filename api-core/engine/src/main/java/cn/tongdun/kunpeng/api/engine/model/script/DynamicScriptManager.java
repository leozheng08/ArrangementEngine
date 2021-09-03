package cn.tongdun.kunpeng.api.engine.model.script;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 *
 * @Author: huangjin
 */
public interface DynamicScriptManager {

    boolean execute(AbstractFraudContext context, IRiskResponse response, RiskRequest request);

}
