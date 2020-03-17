package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
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
public class RuleReLoadManager implements IReload<RuleDO> {

    private Logger logger = LoggerFactory.getLogger(RuleReLoadManager.class);

    @Autowired
    private IRuleRepository ruleRepository;

    @Autowired
    private SubPolicyReLoadManager subPolicyReLoadManager;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    private RuleConvertor ruleConvertor;


    @PostConstruct
    public void init(){
        reloadFactory.register(RuleDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(RuleDO ruleDO){
        String uuid = ruleDO.getUuid();
        logger.debug("Rule reload start, uuid:{}",uuid);
        try {
            Long timestamp = ruleDO.getGmtModify().getTime();
            Rule oldRule = ruleCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldRule != null && oldRule.getModifiedVersion() >= timestamp) {
                logger.debug("Rule reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            RuleDTO ruleDTO = ruleRepository.queryFullByUuid(uuid);

            //如果失效则删除缓存
            if(ruleDTO == null || CommonStatusEnum.CLOSE.getCode() == ruleDTO.getStatus()){
                return remove(ruleDO);
            }

            Rule newRule = ruleConvertor.convert(ruleDTO);
            ruleCache.put(uuid,newRule);

            //刷新子策略下规则的执行顺序
            subPolicyReLoadManager.reloadByUuid(ruleDTO.getBizUuid());
        } catch (Exception e){
            logger.error("Rule reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("Rule reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param ruleDO
     * @return
     */
    @Override
    public boolean remove(RuleDO ruleDO){
        try {
            Rule rule = ruleCache.remove(ruleDO.getUuid());
            if(rule == null){
                return true;
            }
            //刷新子策略下规则的执行顺序
            subPolicyReLoadManager.reloadByUuid(rule.getBizUuid());
        } catch (Exception e){
            logger.error("Rule remove failed, uuid:{}",ruleDO.getUuid(),e);
            return false;
        }
        logger.debug("Rule remove success, uuid:{}",ruleDO.getUuid());
        return true;
    }

    /**
     * 关闭状态
     * @param ruleDO
     * @return
     */
    @Override
    public boolean deactivate(RuleDO ruleDO){
        return remove(ruleDO);
    }

}
