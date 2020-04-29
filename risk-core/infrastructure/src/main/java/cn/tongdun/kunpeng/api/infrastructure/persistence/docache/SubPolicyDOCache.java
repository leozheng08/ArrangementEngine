package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.SubPolicyDAO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import cn.tongdun.kunpeng.share.dataobject.SubPolicyDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class SubPolicyDOCache extends AbstractDataObjectCache<SubPolicyDO> {

    @Autowired
    private SubPolicyDAO subPolicyDAO;


    @Override
    public void refresh(String uuid) {
        SubPolicyDO subPolicyDO = subPolicyDAO.selectByUuid(uuid);
        set(subPolicyDO);
    }

    @Override
    public void refreshAll() {
        List<SubPolicyDO> list = subPolicyDAO.selectAll();
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
    public void setByIdx(SubPolicyDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_policyUuid",0,dataObject.getPolicyUuid()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(SubPolicyDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_policyUuid",dataObject.getPolicyUuid()+":"+dataObject.getUuid());
    }
}
