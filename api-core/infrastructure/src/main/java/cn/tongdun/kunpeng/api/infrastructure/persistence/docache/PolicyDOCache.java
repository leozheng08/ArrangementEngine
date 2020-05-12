package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class PolicyDOCache extends AbstractDataObjectCache<PolicyDO> {

    @Autowired
    private PolicyDAO policyDAO;


    @Override
    public void refresh(String uuid) {
        PolicyDO policyDO = policyDAO.selectByUuid(uuid);
        set(policyDO);
    }


    @Override
    public void refreshAll() {
        List<PolicyDO> list = policyDAO.selectAll();
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
    public void setByIdx(PolicyDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_partner",0,dataObject.getPartnerCode()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(PolicyDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_partner",dataObject.getPartnerCode()+":"+dataObject.getUuid());
    }


    @Override
    public boolean isValid(PolicyDO dataObject){
        if(dataObject.getStatus() != null && dataObject.getStatus().equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if(dataObject.isDeleted()) {
            return false;
        }

        return true;
    }
}
