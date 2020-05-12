package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyChallengerDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDAO;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.InterfaceDefinitionDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyChallengerDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class PolicyChallengerDOCache extends AbstractDataObjectCache<PolicyChallengerDO> {

    @Autowired
    private PolicyChallengerDAO policyChallengerDAO;


    @Override
    public void refresh(String uuid) {
        PolicyChallengerDO policyChallengerDO = policyChallengerDAO.selectByUuid(uuid);
        set(policyChallengerDO);
    }

    @Override
    public void refreshAll() {
        List<PolicyChallengerDO> list = policyChallengerDAO.selectAll();
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
    public void setByIdx(PolicyChallengerDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_policyDefinitionUuid",0,dataObject.getPolicyDefinitionUuid()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(PolicyChallengerDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_policyDefinitionUuid",dataObject.getPolicyDefinitionUuid()+":"+dataObject.getUuid());
    }


    @Override
    public boolean isValid(PolicyChallengerDO dataObject){
        if(dataObject.getStatus() != null && dataObject.getStatus().equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if(dataObject.isDeleted()) {
            return false;
        }

        return true;
    }
}
