package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.IndexDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class IndexDefinitionDOCache extends AbstractDataObjectCache<IndexDefinitionDO> {

    @Autowired
    private IndexDefinitionDAO indexDefinitionDAO;


    @Override
    public void refresh(String uuid) {
        IndexDefinitionDO indexDefinitionDO = indexDefinitionDAO.selectByUuid(uuid);
        set(indexDefinitionDO);
    }


    @Override
    public void refreshAll() {
        List<IndexDefinitionDO> list = indexDefinitionDAO.selectAll();
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
    public void setByIdx(IndexDefinitionDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_subPolicyUuid",0,dataObject.getSubPolicyUuid()+":"+dataObject.getUuid());
        scoreKVRepository.zadd(cacheKey+"_policyUuid",0,dataObject.getPolicyUuid()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(IndexDefinitionDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_subPolicyUuid",dataObject.getSubPolicyUuid()+":"+dataObject.getUuid());
        scoreKVRepository.zrem(cacheKey+"_policyUuid",dataObject.getPolicyUuid()+":"+dataObject.getUuid());
    }


    @Override
    public boolean isValid(IndexDefinitionDO dataObject){
        if(dataObject.getStatus() != null && dataObject.getStatus().equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if(dataObject.isDeleted()) {
            return false;
        }

        return true;
    }
}
