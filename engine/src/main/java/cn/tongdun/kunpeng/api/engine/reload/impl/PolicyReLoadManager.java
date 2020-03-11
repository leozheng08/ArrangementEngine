package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
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
public class PolicyReLoadManager implements IReload<PolicyDO> {

    private Logger logger = LoggerFactory.getLogger(PolicyReLoadManager.class);


    @Autowired
    private IPolicyRepository policyRepository;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private PolicyConvertor policyConvertor;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private DecisionModeCache decisionModeCache;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Autowired
    private SubPolicyReLoadManager subPolicyReLoadManager;

    @PostConstruct
    public void init(){
        reloadFactory.register(PolicyDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(PolicyDO policyDO){
        String uuid = policyDO.getUuid();
        logger.debug("SubPolicyReLoadManager start, uuid:{}",uuid);
        try {
            Long timestamp = policyDO.getGmtModify().getTime();
            Policy oldPolicy = policyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(oldPolicy != null && oldPolicy.getModifiedVersion() >= timestamp) {
                return true;
            }

            PolicyDTO policyDTO = policyRepository.queryByUuid(uuid);
            Policy policy = policyConvertor.convert(policyDTO);
            policyCache.put(uuid,policy);
        } catch (Exception e){
            logger.error("SubPolicyReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("SubPolicyReLoadManager success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param policyDO
     * @return
     */
    @Override
    public boolean remove(PolicyDO policyDO){
        try {
            String policyUuid = policyDO.getUuid();
            removePolicy(policyUuid);
        } catch (Exception e){
            return false;
        }
        return true;
    }


    public boolean removePolicy(String policyUuid){
        //级联删除各个子对象

        Policy policy = policyCache.get(policyUuid);
        if(policy == null){
            return true;
        }
        //删除策略
        policyCache.remove(policyUuid);

        //删除策略运行模式
        decisionModeCache.remove(policyUuid);

        policyIndexCache.removeList(policyUuid);

        List<SubPolicy> subPolicyList = subPolicyCache.getSubPolicyByPolicyUuid(policyUuid);
        if(subPolicyList == null) {
            return true;
        }
        //删除子策略
        for(SubPolicy subPolicy:subPolicyList){
            subPolicyReLoadManager.removeSubPolicy(subPolicy.getUuid());
        }
        return true;
    }
}
