package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDecisionModeDAO;
import cn.tongdun.kunpeng.share.dataobject.PolicyChallengerDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class PolicyDecisionModeDOCache extends AbstractDataObjectCache<PolicyDecisionModeDO> {

    @Autowired
    private PolicyDecisionModeDAO policyDecisionModeDAO;


    @Override
    public void refresh(String uuid) {
        PolicyDecisionModeDO policyDecisionModeDO = policyDecisionModeDAO.selectByUuid(uuid);
        set(policyDecisionModeDO);
    }

    @Override
    public void refreshAll() {
        List<PolicyDecisionModeDO> list = policyDecisionModeDAO.selectAll();
        if(list == null || list.isEmpty()){
            return;
        }
        list.forEach(dataObject ->{
            set(dataObject);
        });
    }

    /**
     * 添加索引
     * @param dataObject
     */
    @Override
    public void setByIdx(PolicyDecisionModeDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_policyUuid",0,dataObject.getPolicyUuid()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(PolicyDecisionModeDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_policyUuid",dataObject.getPolicyUuid()+":"+dataObject.getUuid());
    }
}
