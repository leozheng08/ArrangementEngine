package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.script.IPolicyScriptConfigRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyScriptConfigDAO;
import cn.tongdun.kunpeng.share.dataobject.PolicyScriptConfigDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liupei
 */
@Repository
public class PolicyScriptConfigRepository implements IPolicyScriptConfigRepository {

    @Autowired
    private PolicyScriptConfigDAO policyScriptConfigDAO;

    @Override
    public List<String> queryByPolicyUuid(String policyUuid) {
        List<PolicyScriptConfigDO> policyScriptConfigDOList = policyScriptConfigDAO.selectEnabledByPolicyUuid(policyUuid);
        if (CollectionUtils.isEmpty(policyScriptConfigDOList)) {
            return Lists.newArrayList();
        }
        return policyScriptConfigDOList.stream().map(PolicyScriptConfigDO::getDynamicScriptUuid).collect(Collectors.toList());

    }
}
