package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.BizTypeEnum;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.RuleActionElementDO;
import cn.tongdun.kunpeng.share.dataobject.RuleConditionElementDO;
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
public class RuleActionElementReLoadManager implements IReload<RuleActionElementDO> {

    private Logger logger = LoggerFactory.getLogger(RuleActionElementReLoadManager.class);

    @Autowired
    private IRuleRepository ruleRepository;

    private SubPolicyReLoadManager subPolicyReLoadManager;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private RuleConvertor ruleConvertor;


    @PostConstruct
    public void init(){
        reloadFactory.register(RuleActionElementDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(RuleActionElementDO ruleActionElementDO){
        return reload(ruleActionElementDO);
    }


    /**
     * 条件修改，需要整体规则重新加载
     * @param ruleActionElementDO
     * @return
     */
    public boolean reload(RuleActionElementDO ruleActionElementDO){
        String ruleUuid = ruleActionElementDO.getRuleUuid();
        logger.debug("RuleConditionElementReLoadManager start, ruleUuid:{}",ruleUuid);
        try {

            Long timestamp = ruleActionElementDO.getGmtModify().getTime();
            Rule oldRule = ruleCache.get(ruleUuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldRule != null && oldRule.getModifiedVersion() >= timestamp) {
                return true;
            }

            RuleDTO ruleDTO = ruleRepository.queryFullByUuid(ruleUuid);
            if(ruleDTO == null){
                return true;
            }
            Rule newRule = ruleConvertor.convert(ruleDTO);
            ruleCache.put(ruleUuid,newRule);

            //刷新子策略下规则的执行顺序
            subPolicyReLoadManager.reloadByUuid(ruleDTO.getBizUuid());
        } catch (Exception e){
            logger.error("RuleConditionElementReLoadManager failed, uuid:{}",ruleUuid,e);
            return false;
        }
        logger.debug("RuleConditionElementReLoadManager success, uuid:{}",ruleUuid);
        return true;
    }
    

    /**
     * 删除事件类型
     * @param ruleActionElementDO
     * @return
     */
    @Override
    public boolean remove(RuleActionElementDO ruleActionElementDO){
        return reload(ruleActionElementDO);
    }

}
