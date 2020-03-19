package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.PolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.constant.DeleteStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policyindex.PolicyIndexCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyDefinitionDO;
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
        logger.debug("Policy reload start, uuid:{}",uuid);
        try {
            Long timestamp = policyDO.getGmtModify().getTime();
            Policy oldPolicy = policyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldPolicy != null && oldPolicy.getModifiedVersion() >= timestamp) {
                logger.debug("Policy reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            PolicyDTO policyDTO = policyRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if(policyDTO == null || CommonStatusEnum.CLOSE.getCode() == policyDTO.getStatus()){
                return remove(policyDO);
            }

            Policy policy = policyConvertor.convert(policyDTO);
            policyCache.put(uuid,policy);
        } catch (Exception e){
            logger.error("Policy reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("Policy reload success, uuid:{}",uuid);
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
            logger.error("Policy remove failed, uuid:{}",policyDO.getUuid(),e);
            return false;
        }
        logger.debug("Policy remove success, uuid:{}",policyDO.getUuid());
        return true;
    }


    /**
     * 级联删除各个子对象
     * @param policyUuid
     * @return
     */
    public boolean removePolicy(String policyUuid){
        Policy policy = policyCache.get(policyUuid);
        if(policy == null){
            return true;
        }

        //标记为删除状态
        policy.setDeleted(DeleteStatusEnum.INVALID.getCode());

        //级联删除各个子对象
        return cascadeRemove(policyUuid);
    }

    public boolean cascadeRemove(String policyUuid){

        if(StringUtils.isBlank(policyUuid)){
            return true;
        }

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

    /**
     * 关闭状态
     * @param policyDO
     * @return
     */
    @Override
    public boolean deactivate(PolicyDO policyDO){
        try {
            String policyUuid = policyDO.getUuid();
            Policy policy = policyCache.get(policyUuid);
            if(policy == null){
                return true;
            }

            //标记不在用状态
            policy.setStatus(CommonStatusEnum.CLOSE.getCode());

            //级联删除各个子对象
            cascadeRemove(policyUuid);
        } catch (Exception e){
            logger.error("Policy deactivate failed, uuid:{}",policyDO.getUuid(),e);
            return false;
        }
        logger.debug("Policy deactivate success, uuid:{}",policyDO.getUuid());
        return true;
    }




}
