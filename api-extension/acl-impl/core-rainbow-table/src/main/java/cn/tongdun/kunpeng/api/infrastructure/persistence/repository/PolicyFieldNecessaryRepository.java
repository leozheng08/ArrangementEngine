package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldNecessaryDTO;
import cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary.IPolicyFieldNecessaryRepository;
import cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary.PolicyFieldNecessary;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/1 7:04 下午
 */
@Repository
public class PolicyFieldNecessaryRepository implements IPolicyFieldNecessaryRepository {

    @Override
    public List<PolicyFieldNecessaryDTO> queryByPolicyDefinitionUuid(String policyDefinitionUuid) {
        return null;
    }
}
