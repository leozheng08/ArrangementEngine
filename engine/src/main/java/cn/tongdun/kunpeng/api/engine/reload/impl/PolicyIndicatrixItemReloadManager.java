package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyIndexConvertor;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.IndexDefinitionDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/11 下午3:56
 */
@Component
public class PolicyIndicatrixItemReloadManager implements IReload<PolicyIndicatrixItemDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyIndicatrixItemReloadManager.class);

    @Autowired
    private IPlatformIndexRepository policyIndicatrixItemRepository;

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Autowired
    private PolicyIndexConvertor policyIndexConvertor;
    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(PolicyIndicatrixItemDO.class,this);
    }

    @Override
    public boolean create(PolicyIndicatrixItemDO policyIndicatrixItemDO){
        return addOrUpdate(policyIndicatrixItemDO);
    }
    @Override
    public boolean update(PolicyIndicatrixItemDO policyIndicatrixItemDO){
        return addOrUpdate(policyIndicatrixItemDO);
    }
    @Override
    public boolean activate(PolicyIndicatrixItemDO policyIndicatrixItemDO){
        return addOrUpdate(policyIndicatrixItemDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(PolicyIndicatrixItemDO policyIndicatrixItemDO){
        return reload(policyIndicatrixItemDO.getPolicyUuid());
    }

    public boolean reload(String policyUuid){
        logger.debug("PlatformIndex reload start, policyUuid:{}",policyUuid);
        try {
            List<String> policyIndicatrixItemDTOList = policyIndicatrixItemRepository.queryByPolicyUuid(policyUuid);
            policyIndicatrixItemCache.putList(policyUuid,policyIndicatrixItemDTOList);

        } catch (Exception e){
            logger.error("PlatformIndex reload failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        logger.debug("PlatformIndex reload success, policyUuid:{}",policyUuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param policyIndicatrixItemDO
     * @return
     */
    @Override
    public boolean remove(PolicyIndicatrixItemDO policyIndicatrixItemDO){
        return reload(policyIndicatrixItemDO.getPolicyUuid());
    }

    /**
     * 关闭状态
     * @param policyIndicatrixItemDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyIndicatrixItemDO policyIndicatrixItemDO){
        return remove(policyIndicatrixItemDO);
    }

}
