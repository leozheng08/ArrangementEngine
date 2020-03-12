package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.SubPolicyConvertor;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.ISubPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
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

    @PostConstruct
    public void init(){
        reloadFactory.register(SubPolicyDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(SubPolicyDO subPolicyDO){
        String uuid = subPolicyDO.getUuid();
        logger.debug("SubPolicyReLoadManager start, uuid:{}",uuid);
        try {
            Long timestamp = subPolicyDO.getGmtModify().getTime();
            SubPolicy oldSubPolicy = subPolicyCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(oldSubPolicy != null && oldSubPolicy.getModifiedVersion() >= timestamp) {
                return true;
            }

            reloadByUuid(uuid);
        } catch (Exception e){
            logger.error("SubPolicyReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("SubPolicyReLoadManager success, uuid:{}",uuid);
        return true;
    }

    public void reloadByUuid(String subPolicyUuid){
        SubPolicyDTO subPolicyDTO = subPolicyRepository.queryByUuid(subPolicyUuid);
        if(subPolicyDTO == null){
            return;
        }
        SubPolicy subPolicy = subPolicyConvertor.convert(subPolicyDTO);
        subPolicyCache.put(subPolicyUuid,subPolicy);
    }


    /**
     * 删除事件类型
     * @param subPolicyDO
     * @return
     */
    @Override
    public boolean remove(SubPolicyDO subPolicyDO){
        try {
            return removeSubPolicy(subPolicyDO.getUuid());
        } catch (Exception e){
            return false;
        }
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
}
