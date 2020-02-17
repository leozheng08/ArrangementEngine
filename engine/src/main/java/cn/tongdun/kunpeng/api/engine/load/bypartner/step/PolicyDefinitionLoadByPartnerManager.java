package cn.tongdun.kunpeng.api.engine.load.bypartner.step;

import cn.tongdun.kunpeng.api.engine.load.bypartner.ILoadByPartner;
import cn.tongdun.kunpeng.api.engine.load.bypartner.LoadByPartnerPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadByPartnerPipeline.NAME, phase = LoadByPartnerPipeline.LOAD_POLICY, order=100)
public class PolicyDefinitionLoadByPartnerManager implements ILoadByPartner {

    private Logger logger = LoggerFactory.getLogger(PolicyDefinitionLoadByPartnerManager.class);

    @Autowired
    private IPolicyDefinitionRepository policyDefinitionRepository;

    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;

    @Autowired
    private PartnerClusterCache partnerClusterCache;

    /**
     * 加载当前集群下所有合作方的策略定义
     * @return
     */
    @Override
    public boolean loadByPartner(String partnerCode){
        logger.info("LoadPolicyDefinitionByPartnerManager start");

        //取得策略定义列表
        List<PolicyDefinition> PolicyModifiedDOList = policyDefinitionRepository.queryByPartner(partnerCode);

        for(PolicyDefinition policyDefinition:PolicyModifiedDOList) {
            policyDefinitionCache.put(policyDefinition.getUuid(),policyDefinition);
        }

        logger.info("LoadPolicyDefinitionByPartnerManager success, size:"+PolicyModifiedDOList.size());
        return true;
    }
}
