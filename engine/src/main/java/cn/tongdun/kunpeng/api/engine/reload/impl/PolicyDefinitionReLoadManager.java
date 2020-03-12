package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.load.step.PolicyLoadTask;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class PolicyDefinitionReLoadManager implements IReload<PolicyDefinitionDO> {


    private Logger logger = LoggerFactory.getLogger(PolicyDefinitionReLoadManager.class);

    @Autowired
    private IPolicyDefinitionRepository policyDefinitionRepository;

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;


    @Autowired
    private IPolicyRepository policyRepository;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private DecisionModeCache decisionModeCache;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Autowired
    private DefaultConvertorFactory defaultConvertorFactory;

    @Autowired
    private LocalCacheService localCacheService;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private PolicyReLoadManager policyReLoadManager;

    @Autowired
    private IPlatformIndexRepository policyIndicatrixItemRepository;

    @Autowired
    private PlatformIndexCache policyIndicatrixItemCache;

    @PostConstruct
    public void init(){
        reloadFactory.register(PolicyDefinitionDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(PolicyDefinitionDO policyDefinitionDO){
        String uuid = policyDefinitionDO.getUuid();
        logger.debug("PolicyDefinitionReLoadManager start, uuid:{}",uuid);
        boolean result = false;
        try {
            Long timestamp = policyDefinitionDO.getGmtModify().getTime();
            PolicyDefinition oldPolicyDefinition = policyDefinitionCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldPolicyDefinition != null && oldPolicyDefinition.getModifiedVersion() >= timestamp) {
                return true;
            }

            PolicyDefinition policyDefinition = policyDefinitionRepository.queryByUuid(uuid);
            if(policyDefinition == null){
                return true;
            }
            //当前调用版本
            String newPolicyUuid = policyDefinition.getCurrVersionUuid();
            Policy oldPolicy = policyCache.get(newPolicyUuid);
            if(oldPolicy != null){ //如果已存在，说明没有更新当前调用版本，只更新策略名称即可
                oldPolicy.setName(policyDefinition.getName());
                result = true;
            } else {
                //加载策略信息，包含各个子对象
                PolicyLoadTask task = new PolicyLoadTask(newPolicyUuid,policyRepository,defaultConvertorFactory,localCacheService, policyIndicatrixItemRepository, policyIndicatrixItemCache);
                result = task.call();
            }
            if(result){
                policyDefinitionCache.put(uuid,policyDefinition);
            }

        } catch (Exception e){
            logger.error("PolicyDefinitionReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("PolicyDefinitionReLoadManager success, uuid:{}",uuid);
        return result;
    }


    /**
     * 删除事件类型
     * @param policyDefinitionDO
     * @return
     */
    @Override
    public boolean remove(PolicyDefinitionDO policyDefinitionDO){
        try {
            PolicyDefinition policyDefinition = policyDefinitionCache.remove(policyDefinitionDO.getUuid());
            if(policyDefinition == null){
                return true;
            }
            String policyUuid = policyDefinition.getCurrVersionUuid();

            return policyReLoadManager.removePolicy(policyUuid);

        } catch (Exception e){
            return false;
        }
    }
}
