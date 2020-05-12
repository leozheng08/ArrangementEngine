package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyIndicatrixItemDAO;
import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlatformIndexRepository implements IPlatformIndexRepository {

    @Autowired
    private PolicyIndicatrixItemDAO policyIndicatrixItemDAO;

    @Override
    public List<String> queryByPolicyUuid(String policyUuid) {
        List<PolicyIndicatrixItemDO> indicatrixItemList = policyIndicatrixItemDAO.selectEnabledByPolicyUuid(policyUuid);
        if (null != indicatrixItemList) {
            List<String> result = Lists.newArrayList();
            indicatrixItemList.forEach(item -> {
                result.add(item.getIndicatrixId());
            });
            return result;
        }
        return null;
    }
}
