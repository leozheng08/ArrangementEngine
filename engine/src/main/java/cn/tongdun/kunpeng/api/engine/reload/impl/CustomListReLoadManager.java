package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class CustomListReLoadManager {

    private Logger logger = LoggerFactory.getLogger(CustomListReLoadManager.class);

    @Autowired
    private IPolicyDefinitionRepository policyDefinitionRepository;

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    /**
     * 加载自定义列表数据
     * @return
     */
    public boolean reload(String uuid){
        logger.info("CustomListReLoadManager start, uuid:{}",uuid);

        try {
            PolicyDefinition policyDefinition = policyDefinitionRepository.queryByUuid(uuid);
            policyDefinitionCache.put(policyDefinition.getUuid(), policyDefinition);
        } catch (Exception e){
            logger.error("CustomListReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.info("CustomListReLoadManager success, uuid:{}",uuid);
        return true;
    }



}
