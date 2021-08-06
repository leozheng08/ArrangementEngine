package cn.tongdun.kunpeng.api.engine.model.script;

import java.util.List;

public interface IPolicyScriptConfigRepository {

    /**
     *
     * @param policyUuid
     * @return
     */
    List<String> queryByPolicyUuid(String policyUuid);
}
