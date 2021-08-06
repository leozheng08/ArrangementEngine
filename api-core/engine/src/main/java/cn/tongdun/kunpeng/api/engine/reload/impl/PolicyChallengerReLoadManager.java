package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.load.step.PolicyLoadTask;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.IPolicyChallengerRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallenger;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallengerCache;
import cn.tongdun.kunpeng.api.engine.model.script.IPolicyScriptConfigRepository;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PolicyChallengerEventDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class PolicyChallengerReLoadManager implements IReload<PolicyChallengerEventDO> {


    private Logger logger = LoggerFactory.getLogger(PolicyChallengerReLoadManager.class);

    @Autowired
    private IPolicyChallengerRepository policyChallengerRepository;

    @Autowired
    private PolicyChallengerCache policyChallengerCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private PolicyConvertor policyConvertor;

    @Autowired
    private ReloadFactory reloadFactory;


    @Autowired
    private IPolicyRepository policyRepository;
    @Autowired
    private DefaultConvertorFactory defaultConvertorFactory;
    @Autowired
    private LocalCacheService localCacheService;
    @Autowired
    private IPlatformIndexRepository platformIndexRepository;
    @Autowired
    private PlatformIndexCache platformIndexCache;
    @Autowired
    private BatchRemoteCallDataCache batchRemoteCallDataCache;
    @Autowired
    private IPolicyScriptConfigRepository policyScriptConfigRepository;
    @Autowired
    private GroovyObjectCache groovyObjectCache;


    @PostConstruct
    public void init() {
        reloadFactory.register(PolicyChallengerEventDO.class, this);
    }

    @Override
    public boolean create(PolicyChallengerEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean update(PolicyChallengerEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean activate(PolicyChallengerEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean remove(PolicyChallengerEventDO eventDO) {
        try {
            return removeByPolicyChallenger(policyChallengerRepository.queryByUuid(eventDO.getUuid()));
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "PolicyChallenger removeByPolicyChallenger failed", e);
            return false;
        }
    }

    @Override
    public boolean deactivate(PolicyChallengerEventDO eventDO) {
        return remove(eventDO);
    }

    @Override
    public boolean suspend(PolicyChallengerEventDO eventDO) {
        return addOrUpdate(eventDO);
    }

    @Override
    public boolean terminate(PolicyChallengerEventDO eventDO) {
        return remove(eventDO);
    }

    private boolean removeByPolicyChallenger(PolicyChallenger policyChallenger) {
        if (policyChallenger == null) {
            return true;
        }
        policyChallengerCache.remove(policyChallenger.getPolicyDefinitionUuid());
        return true;
    }

    /**
     * 更新事件类型
     *
     * @return
     */
    public boolean addOrUpdate(PolicyChallengerEventDO eventDO) {
        String uuid = eventDO.getUuid();
        logger.debug(TraceUtils.getFormatTrace() + "PolicyChallenger reload start, uuid:{}", uuid);
        boolean result = false;
        try {
            PolicyChallenger policyChallenger = policyChallengerRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if (policyChallenger == null || !policyChallenger.isValid()) {
                return remove(eventDO);
            }

            policyChallengerCache.put(policyChallenger.getPolicyDefinitionUuid(), policyChallenger);

            //加载挑战者策略信息
            loadPolicy(policyChallenger);
            result = true;
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "PolicyChallenger reload failed, uuid:{}", uuid, e);
            return false;
        }
        logger.debug(TraceUtils.getFormatTrace() + "PolicyChallenger reload success, uuid:{}", uuid);
        return result;
    }


    /**
     * 加载挑战者策略信息
     *
     * @param policyChallenger
     */
    private void loadPolicy(PolicyChallenger policyChallenger) {
        List<PolicyChallenger.Config> configs = policyChallenger.getChallengerConfig();
        if (configs == null || configs.isEmpty()) {
            return;
        }

        for (PolicyChallenger.Config config : configs) {
            if (StringUtils.isBlank(config.getVersionUuid())) {
                continue;
            }
            Policy policy = policyCache.get(config.getVersionUuid());
            if (policy == null) { //如果原先策略未加载过，则加载到内存
                PolicyDTO policyDTO = policyRepository.queryByUuid(config.getVersionUuid());
                if (policyDTO == null) {
                    continue;
                }
                //如果失效仍缓存，便于返回404子码
                if (!policyDTO.isValid()) {
                    policy = policyConvertor.convert(policyDTO);
                    policyCache.put(policy.getUuid(), policy);
                    continue;
                }

                //加载策略各个子对象信息
                PolicyLoadTask task = new PolicyLoadTask(config.getVersionUuid(), policyRepository, defaultConvertorFactory, localCacheService, platformIndexRepository, platformIndexCache, batchRemoteCallDataCache, policyScriptConfigRepository, groovyObjectCache);
                task.call();
            }
        }
    }
}
