package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
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
    private IPlatformIndexRepository platformIndexRepository;

    @Autowired
    private PlatformIndexCache platformIndexCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init() {
        reloadFactory.register(PolicyIndicatrixItemEventDO.class, this);
    }

    @Override
    public boolean create(PolicyIndicatrixItemEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(PolicyIndicatrixItemEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(PolicyIndicatrixItemEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(PolicyIndicatrixItemEventDO eventDO) {
        return reload(eventDO.getPolicyUuid());
    }

    public boolean reload(String policyUuid) {
        logger.debug(TraceUtils.getFormatTrace() + "PlatformIndex reload start, policyUuid:{}, currentTime={}", policyUuid, System.currentTimeMillis());
        try {
            List<String> policyIndicatrixItemDTOList = platformIndexRepository.queryByPolicyUuid(policyUuid);
            platformIndexCache.putList(policyUuid, policyIndicatrixItemDTOList);
            logger.debug(TraceUtils.getFormatTrace() + "PlatformIndex reload end, policyUuid:{}, currentTime={}", policyUuid, System.currentTimeMillis());
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "PlatformIndex reload failed, policyUuid:{}", policyUuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "PlatformIndex reload success, policyUuid:{}", policyUuid);
        return true;
    }


    /**
     * 删除事件类型
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(PolicyIndicatrixItemEventDO eventDO) {
        return reload(eventDO.getPolicyUuid());
    }

    /**
     * 关闭状态
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyIndicatrixItemEventDO eventDO) {
        return remove(eventDO);
    }

}
