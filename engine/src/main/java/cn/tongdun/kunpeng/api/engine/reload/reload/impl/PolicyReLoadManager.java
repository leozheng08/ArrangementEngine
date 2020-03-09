package cn.tongdun.kunpeng.api.engine.reload.reload.impl;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.load.step.PolicyLoadTask;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class PolicyReLoadManager {

    private Logger logger = LoggerFactory.getLogger(PolicyReLoadManager.class);


    @Autowired
    private IPolicyRepository policyRepository;

    @Autowired
    private DefaultConvertorFactory defaultConvertorFactory;

    @Autowired
    private LocalCacheService localCacheService;


    public boolean reload(String uuid){
        PolicyLoadTask task = new PolicyLoadTask(uuid,policyRepository,defaultConvertorFactory,localCacheService);
        boolean isSuccess = task.call();
        return isSuccess;
    }
}
