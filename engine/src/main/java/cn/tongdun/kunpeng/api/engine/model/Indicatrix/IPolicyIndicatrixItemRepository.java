package cn.tongdun.kunpeng.api.engine.model.Indicatrix;

import java.util.List;

public interface IPolicyIndicatrixItemRepository {

    /**
     *
     * @param policyUuid
     * @return
     */
    List<String> queryByPolicyUuid(String policyUuid);
}
