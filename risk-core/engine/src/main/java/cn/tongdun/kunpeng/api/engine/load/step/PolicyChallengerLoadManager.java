package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.IPolicyChallengerRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallenger;
import cn.tongdun.kunpeng.api.engine.model.policy.challenger.PolicyChallengerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_POLICY, order=300)
public class PolicyChallengerLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(PolicyChallengerLoadManager.class);

    @Autowired
    private IPolicyChallengerRepository policyChallengerRepository;

    @Autowired
    private PolicyChallengerCache policyChallengerCache;

    @Autowired
    private PartnerClusterCache partnerClusterCache;

    @Override
    public boolean load(){
        logger.info("PolicyChallengerLoadManager start");

        //取得合作方范围
        Set<String> partners = partnerClusterCache.getPartners();

        //取得挑战者任务列表
        List<PolicyChallenger> policyChallengerList = policyChallengerRepository.queryAvailableByPartners(partners);

        for(PolicyChallenger policyChallenger:policyChallengerList) {
            policyChallengerCache.put(policyChallenger.getUuid(),policyChallenger);
        }

        logger.info("PolicyChallengerLoadManager success, size:"+policyChallengerList.size());
        return true;
    }
}
