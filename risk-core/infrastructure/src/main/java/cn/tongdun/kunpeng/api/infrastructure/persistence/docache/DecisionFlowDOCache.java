package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.DecisionFlowDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDAO;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class DecisionFlowDOCache extends AbstractDataObjectCache<DecisionFlowDO> {

    @Autowired
    private DecisionFlowDAO decisionFlowDAO;


    @Override
    public void refresh(String uuid) {
        DecisionFlowDO decisionFlowDO = decisionFlowDAO.selectByUuid(uuid);
        set(decisionFlowDO);
    }

    @Override
    public void refreshAll() {
        List<DecisionFlowDO> list = decisionFlowDAO.selectAll();
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
    public void setByIdx(DecisionFlowDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        //scoreKVRepository.zadd(cacheKey+"_partner",0,dataObject.getPartnerCode()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(DecisionFlowDO dataObject){
        //scoreKVRepository.zrem(cacheKey+"_partner",dataObject.getPartnerCode()+":"+dataObject.getUuid());
    }
}
