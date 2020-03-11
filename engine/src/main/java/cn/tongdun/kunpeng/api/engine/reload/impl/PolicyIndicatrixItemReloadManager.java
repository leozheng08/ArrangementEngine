package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyIndexConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyIndicatrixItemDTO;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPolicyIndicatrixItemRepository2;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/11 下午3:56
 */
public class PolicyIndicatrixItemReloadManager implements IReload<PolicyIndicatrixItemDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyIndexReLoadManager.class);

    @Autowired
    private IPolicyIndicatrixItemRepository2 policyIndicatrixItemRepository;

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

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(PolicyIndicatrixItemDO policyIndicatrixItemDO){
        return reload(policyIndicatrixItemDO.getPolicyUuid());
    }

    private boolean reload(String policyUuid){
        logger.debug("PolicyIndicatrixItemReloadManager start, policyUuid:{}",policyUuid);
        try {
            List<PolicyIndicatrixItemDTO> policyIndicatrixItemDTOList = policyIndicatrixItemRepository.queryByPolicyUuid(policyUuid);
            //缓存平台指标
//            if (null!= policyIndicatrixItemDTOList && !policyIndicatrixItemDTOList.isEmpty()){
//                policyIndexCache.putList(policyUuid,policyIndicatrixItemDTOList);
//            } else {
//                policyIndexCache.removeList(policyUuid);
//            }

        } catch (Exception e){
            logger.error("PolicyIndicatrixItemReloadManager failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        logger.debug("PolicyIndicatrixItemReloadManager success, policyUuid:{}",policyUuid);
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
}
