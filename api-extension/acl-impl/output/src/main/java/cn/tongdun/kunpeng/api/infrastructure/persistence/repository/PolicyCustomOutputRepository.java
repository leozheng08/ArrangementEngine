package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.dto.PolicyCustomOutputDTO;
import cn.tongdun.kunpeng.api.engine.model.customoutput.IPolicyCustomOutputRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/10/8 19:43
 */
@Repository
public class PolicyCustomOutputRepository implements IPolicyCustomOutputRepository {

    @Override
    public PolicyCustomOutputDTO queryByUuid(String uuid) {
        return null;
    }

    @Override
    public List<PolicyCustomOutputDTO> selectByPolicyUuid(String policyUuid) {
        return new ArrayList<>();
    }
}
