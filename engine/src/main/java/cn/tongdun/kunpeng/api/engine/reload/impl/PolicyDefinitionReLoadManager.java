package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.load.step.PolicyLoadTask;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.constant.DeleteStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    @Override
    public boolean create(PolicyDefinitionDO policyDefinitionDO){
        return addOrUpdate(policyDefinitionDO);
    }
    @Override
    public boolean update(PolicyDefinitionDO policyDefinitionDO){
        return addOrUpdate(policyDefinitionDO);
    }
    @Override
    public boolean activate(PolicyDefinitionDO policyDefinitionDO){
        return addOrUpdate(policyDefinitionDO);
    }

    /**
     * 删除事件类型,子对象都删除，但缓存中仍保留PolicyDefinition对象，用于404子码的区分
     * @param policyDefinitionDO
     * @return
     */
    @Override
    public boolean remove(PolicyDefinitionDO policyDefinitionDO){
        return remove(policyDefinitionDO, policyDefinition -> {
                    //标记删除状态
                    policyDefinition.setDeleted(DeleteStatusEnum.INVALID.getCode());
                }
        );
    }

    /**
     * 关闭状态,子对象都删除，但缓存中仍保留PolicyDefinition对象，用于404子码的区分
     * @param policyDefinitionDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyDefinitionDO policyDefinitionDO){
        return remove(policyDefinitionDO, policyDefinition -> {
                    //标记不在用状态
                    policyDefinition.setStatus(CommonStatusEnum.CLOSE.getCode());
                }
        );
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(PolicyDefinitionDO policyDefinitionDO){
        String uuid = policyDefinitionDO.getUuid();
        logger.debug("PolicyDefinition reload start, uuid:{}",uuid);
        boolean result = false;
        try {
            Long timestamp = policyDefinitionDO.getGmtModify().getTime();
            PolicyDefinition oldPolicyDefinition = policyDefinitionCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldPolicyDefinition != null && oldPolicyDefinition.getModifiedVersion() >= timestamp) {
                logger.debug("PolicyDefinition reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            PolicyDefinition policyDefinition = policyDefinitionRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if(policyDefinition == null || CommonStatusEnum.CLOSE.getCode() == policyDefinition.getStatus()){
                remove(policyDefinitionDO);
                return policyReLoadManager.removePolicy(policyDefinition != null?policyDefinition.getCurrVersionUuid():policyDefinitionDO.getCurrVersionUuid());
            }

            //当前调用版本
            String newPolicyUuid = policyDefinition.getCurrVersionUuid();
            Policy oldPolicy = policyCache.get(newPolicyUuid);
            if(oldPolicyDefinition != null && newPolicyUuid.equals(oldPolicyDefinition.getCurrVersionUuid())
                    && oldPolicy!= null){
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
            logger.error("PolicyDefinition reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("PolicyDefinition reload success, uuid:{}",uuid);
        return result;
    }


    public boolean remove(PolicyDefinitionDO policyDefinitionDO, Consumer<PolicyDefinition> consumer){
        try {
            PolicyDefinition policyDefinition = policyDefinitionCache.get(policyDefinitionDO.getUuid());
            if(policyDefinition == null){
                return policyReLoadManager.removePolicy(policyDefinitionDO.getCurrVersionUuid());
            }

            //对policyDefinition修改相关状态
            consumer.accept(policyDefinition);

            String policyUuid = policyDefinition.getCurrVersionUuid();
            boolean result = policyReLoadManager.removePolicy(policyUuid);

            logger.debug("PolicyDefinition remove success,policyDefinitionUuid:{} policyUuid:{}",policyDefinition.getUuid(),policyUuid);
            return result;
        } catch (Exception e){
            logger.error("PolicyDefinition remove failed, uuid:{}",policyDefinitionDO.getUuid(),e);
            return false;
        }
    }



}
