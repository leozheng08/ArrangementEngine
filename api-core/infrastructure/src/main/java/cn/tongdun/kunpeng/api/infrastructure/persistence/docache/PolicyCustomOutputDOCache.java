package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyCustomOutputDAO;
import cn.tongdun.kunpeng.share.dataobject.PolicyCustomOutputDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/17 10:26
 */
@Component
public class PolicyCustomOutputDOCache extends AbstractDataObjectCache<PolicyCustomOutputDO> {

    @Autowired
    private PolicyCustomOutputDAO policyCustomOutputDAO;

    @Override
    public void setByIdx(PolicyCustomOutputDO dataObject) {
        scoreKVRepository.zadd(cacheKey+"_policyDefinitionUuid",0,dataObject.getPolicyDefinitionUuid()+":"+dataObject.getUuid());
    }

    @Override
    public void removeIdx(PolicyCustomOutputDO dataObject) {
        scoreKVRepository.zrem(cacheKey+"_policyDefinitionUuid",dataObject.getPolicyDefinitionUuid()+":"+dataObject.getUuid());
    }

    @Override
    public boolean isValid(PolicyCustomOutputDO dataObject) {
        if(dataObject.getStatus() != null && dataObject.getStatus().equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if(dataObject.isDeleted()) {
            return false;
        }

        return true;
    }

    @Override
    public void refresh(String uuid) {
        PolicyCustomOutputDO customOutputDO = policyCustomOutputDAO.queryByUuid(uuid);
        set(customOutputDO);
    }

    @Override
    public void refreshAll() {
        List<PolicyCustomOutputDO> list = policyCustomOutputDAO.queryAll();
        if(list == null || list.isEmpty()){
            return;
        }
        list.forEach(dataObject ->{
            set(dataObject);
        });
    }
}
