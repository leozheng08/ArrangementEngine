package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDecisionModeDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class PolicyDefinitionDOCache extends AbstractDataObjectCache<PolicyDefinitionDO> {

    @Autowired
    private PolicyDefinitionDAO policyDefinitionDAO;


    /**
     * 按uuid从数据库中查询后刷新redis缓存
     * @param uuid
     */
    @Override
    public void refresh(String uuid) {
        PolicyDefinitionDO policyDefinitionDO = policyDefinitionDAO.selectByUuid(uuid);
        set(policyDefinitionDO);
    }


    /**
     * 从数据库中查询后所有刷新redis缓存，用于数据初始化
     * todo 后面优化按分页加载
     */
    @Override
    public void refreshAll() {
        List<PolicyDefinitionDO> list = policyDefinitionDAO.selectAll();
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
    public void setByIdx(PolicyDefinitionDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_partner",0,dataObject.getPartnerCode()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(PolicyDefinitionDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_partner",dataObject.getPartnerCode()+":"+dataObject.getUuid());
    }


    @Override
    public boolean isValid(PolicyDefinitionDO dataObject){
        if(dataObject.getStatus() != null && dataObject.getStatus().equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if(dataObject.isDeleted()) {
            return false;
        }

        return true;
    }


}
