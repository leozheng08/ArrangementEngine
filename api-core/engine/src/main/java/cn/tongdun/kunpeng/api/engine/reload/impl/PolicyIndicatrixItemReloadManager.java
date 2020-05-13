package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyIndexConvertor;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyIndicatrixItemEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
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
public class PolicyIndicatrixItemReloadManager implements IReload<PolicyIndicatrixItemEventDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyIndicatrixItemReloadManager.class);

    @Autowired
    private IPlatformIndexRepository policyIndicatrixItemRepository;

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(PolicyIndicatrixItemEventDO.class,this);
    }

    @Override
    public boolean create(PolicyIndicatrixItemEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean update(PolicyIndicatrixItemEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean activate(PolicyIndicatrixItemEventDO eventDO){
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(PolicyIndicatrixItemEventDO eventDO){
        return reload(eventDO.getPolicyUuid());
    }

    public boolean reload(String policyUuid){
        logger.debug(TraceUtils.getFormatTrace()+"PlatformIndex reload start, policyUuid:{}",policyUuid);
        try {
            List<String> policyIndicatrixItemDTOList = policyIndicatrixItemRepository.queryByPolicyUuid(policyUuid);
            policyIndicatrixItemCache.putList(policyUuid,policyIndicatrixItemDTOList);

        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"PlatformIndex reload failed, policyUuid:{}",policyUuid,e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace()+"PlatformIndex reload success, policyUuid:{}",policyUuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(PolicyIndicatrixItemEventDO eventDO){
        return reload(eventDO.getPolicyUuid());
    }

    /**
     * 关闭状态
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyIndicatrixItemEventDO eventDO){
        return remove(eventDO);
    }

}
