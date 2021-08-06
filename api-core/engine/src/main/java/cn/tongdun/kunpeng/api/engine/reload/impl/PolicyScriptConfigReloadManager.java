package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.engine.model.script.IPolicyScriptConfigRepository;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyIndicatrixItemEventDO;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyScriptConfigEventDO;
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
public class PolicyScriptConfigReloadManager implements IReload<PolicyScriptConfigEventDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyScriptConfigReloadManager.class);

    @Autowired
    private IPolicyScriptConfigRepository policyScriptConfigRepository;

    @Autowired
    private GroovyObjectCache groovyObjectCache;

    @Autowired
    private ReloadFactory reloadFactory;


    @PostConstruct
    public void init() {
        reloadFactory.register(PolicyScriptConfigEventDO.class, this);
    }

    @Override
    public boolean create(PolicyScriptConfigEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(PolicyScriptConfigEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(PolicyScriptConfigEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(PolicyScriptConfigEventDO eventDO) {
        return reload(eventDO.getPolicyUuid());
    }

    public boolean reload(String policyUuid) {
        logger.debug(TraceUtils.getFormatTrace() + "PolicyScript reload start, policyUuid:{}", policyUuid);
        try {
            List<String> policyScriptConfigList = policyScriptConfigRepository.queryByPolicyUuid(policyUuid);
            groovyObjectCache.putList(policyUuid, policyScriptConfigList);

        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "PolicyScript reload failed, policyUuid:{}", policyUuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "PolicyScript reload success, policyUuid:{}", policyUuid);
        return true;
    }


    /**
     * 删除事件类型
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(PolicyScriptConfigEventDO eventDO) {
        return reload(eventDO.getPolicyUuid());
    }

    /**
     * 关闭状态
     *
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyScriptConfigEventDO eventDO) {
        return remove(eventDO);
    }

}
