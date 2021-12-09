package cn.tongdun.kunpeng.api.engine.model.customoutput;


import cn.tongdun.kunpeng.api.engine.dto.PolicyCustomOutputDTO;

import java.util.List;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/12 21:45
 */
public interface IPolicyCustomOutputRepository {

    PolicyCustomOutputDTO queryByUuid(String uuid);


    List<PolicyCustomOutputDTO> selectByPolicyUuid(String policyUuid);

}
