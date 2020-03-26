package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.SubPolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.ISubPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.PolicyDO;
import cn.tongdun.kunpeng.share.dataobject.SubPolicyDO;
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
public class SubPolicyReLoadManager implements IReload<SubPolicyDO> {

    private Logger logger = LoggerFactory.getLogger(SubPolicyReLoadManager.class);

    @Autowired
    private ISubPolicyRepository subPolicyRepository;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    private SubPolicyConvertor subPolicyConvertor;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private PolicyIndicatrixItemReloadManager policyIndicatrixItemReloadManager;

    @PostConstruct
    public void init(){
        reloadFactory.register(SubPolicyDO.class,this);
    }

    @Override
    public boolean create(SubPolicyDO subPolicyDO){
        return addOrUpdate(subPolicyDO);
    }
    @Override
    public boolean update(SubPolicyDO subPolicyDO){
        return addOrUpdate(subPolicyDO);
    }
    @Override
    public boolean activate(SubPolicyDO subPolicyDO){
        return addOrUpdate(subPolicyDO);
    }


    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(SubPolicyDO subPolicyDO){
        String uuid = subPolicyDO.getUuid();
        logger.debug("SubPolicy reload start, uuid:{}",uuid);
        try {
            Long timestamp = subPolicyDO.getGmtModify().getTime();
            SubPolicy oldSubPolicy = subPolicyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldSubPolicy != null && oldSubPolicy.getModifiedVersion() >= timestamp) {
                logger.debug("SubPolicy reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            reloadByUuid(uuid);
        } catch (Exception e){
            logger.error("SubPolicy reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("SubPolicy reload success, uuid:{}",uuid);
        return true;
    }

    public void reloadByUuid(String subPolicyUuid){
        SubPolicyDTO subPolicyDTO = subPolicyRepository.queryByUuid(subPolicyUuid);

        //如果失效则删除缓存
        if(subPolicyDTO == null || CommonStatusEnum.CLOSE.getCode() == subPolicyDTO.getStatus()){
            removeSubPolicy(subPolicyUuid);
            return;
        }

        SubPolicy subPolicy = subPolicyConvertor.convert(subPolicyDTO);
        subPolicyCache.put(subPolicyUuid,subPolicy);

        //刷新引用到的平台指标
        policyIndicatrixItemReloadManager.reload(subPolicyDTO.getPolicyUuid());
    }


    /**
     * 删除事件类型
     * @param subPolicyDO
     * @return
     */
    @Override
    public boolean remove(SubPolicyDO subPolicyDO){
        try {
            boolean result = removeSubPolicy(subPolicyDO.getUuid());
            logger.debug("SubPolicy remove success, uuid:{}",subPolicyDO.getUuid());
            return result;
        } catch (Exception e){
            logger.error("SubPolicy remove failed, uuid:{}",subPolicyDO.getUuid(),e);
            return false;
        }
    }

    /**
     * 关闭状态
     * @param subPolicyDO
     * @return
     */
    @Override
    public boolean deactivate(SubPolicyDO subPolicyDO){
        return remove(subPolicyDO);
    }

    public boolean removeSubPolicy(String subPolicyUuid){
        SubPolicy subPolicy = subPolicyCache.remove(subPolicyUuid);
        if(subPolicy == null){
            return true;
        }

        List<Rule> ruleList = ruleCache.getRuleBySubPolicyUuid(subPolicy.getUuid());
        if(ruleList == null) {
            return true;
        }
        for(Rule rule:ruleList) {
            //删除规则
            ruleCache.remove(rule.getUuid());
        }
        return true;
    }


    /**
     * 子策略排序，对当前执行暂无影响，不处理
     * @param list
     * @return
     */
    @Override
    public boolean sort(List<SubPolicyDO> list){
        return true;
    }
}
