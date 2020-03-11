package cn.tongdun.kunpeng.api.engine.reload.reload.impl;

import cn.tongdun.kunpeng.api.engine.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.reload.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
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
        logger.info("RuleReLoadManager start, uuid:{}",uuid);
        try {
            Long timestamp = ruleDO.getGmtModify().getTime();
            Rule oldRule = ruleCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(oldRule != null && oldRule.getModifiedVersion() >= timestamp) {
                return true;
            }

            RuleDTO ruleDTO = ruleRepository.queryFullByUuid(uuid);
            Rule newRule = ruleConvertor.convert(ruleDTO);
            ruleCache.put(uuid,newRule);
        } catch (Exception e){
            logger.error("RuleReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.info("RuleReLoadManager success, uuid:{}",uuid);
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
            ruleCache.remove(ruleDO.getUuid());
        } catch (Exception e){
            return false;
        }
        return true;
    }

}
