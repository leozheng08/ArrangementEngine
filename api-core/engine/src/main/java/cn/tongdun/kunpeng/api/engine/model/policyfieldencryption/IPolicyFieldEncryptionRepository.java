package cn.tongdun.kunpeng.api.engine.model.policyfieldencryption;

import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldEncryptionDTO;
import cn.tongdun.kunpeng.share.dataobject.PolicyFieldEncryptionDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/1 5:55 下午
 */
public interface IPolicyFieldEncryptionRepository {
    /**
     * 根据策略uuid查询
     * @param policyDefinitionUuid
     * @return
     */
    List<PolicyFieldEncryptionDTO> queryByPolicyDefinitionUuid(String policyDefinitionUuid);
}
