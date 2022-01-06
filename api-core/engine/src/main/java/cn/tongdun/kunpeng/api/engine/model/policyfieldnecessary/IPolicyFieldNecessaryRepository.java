package cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary;

import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldNecessaryDTO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/1 5:55 下午
 */
public interface IPolicyFieldNecessaryRepository {
    /**
     * 根据策略uuid查询必选字段
     *
     * @param policyDefinitionUuid
     * @return
     */
    List<PolicyFieldNecessaryDTO> queryByPolicyDefinitionUuid(String policyDefinitionUuid);
}
