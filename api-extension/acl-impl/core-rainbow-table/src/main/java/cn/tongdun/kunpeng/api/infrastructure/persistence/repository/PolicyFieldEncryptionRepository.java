package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldEncryptionDTO;
import cn.tongdun.kunpeng.api.engine.model.policyfieldencryption.IPolicyFieldEncryptionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/1 7:15 下午
 */
@Repository
public class PolicyFieldEncryptionRepository implements IPolicyFieldEncryptionRepository {
    @Override
    public List<PolicyFieldEncryptionDTO> queryByPolicyDefinitionUuid(String policyDefinitionUuid) {
        return null;
    }
}
