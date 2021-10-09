package cn.tongdun.kunpeng.api.engine.model.customoutput;


import cn.tongdun.kunpeng.api.engine.dto.PolicyCustomOutputDTO;

import java.util.List;
import java.util.Set;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/12 21:45
 */
public interface IPolicyCustomOutputRepository {

    PolicyCustomOutput queryByUuid(String uuid);


    List<PolicyCustomOutput> selectByPolicyDefinitionUuid(String policyDefinitionUuid);

    /**
     * 根据合作方查询自定义输出
     * @param partners
     * @return
     */
    List<PolicyCustomOutput> selectByPartners(Set<String> partners);
}
