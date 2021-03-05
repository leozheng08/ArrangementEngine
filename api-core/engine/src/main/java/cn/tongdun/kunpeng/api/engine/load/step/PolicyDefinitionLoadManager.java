package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.IPolicyDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
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
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_POLICY, order=100)
public class PolicyDefinitionLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(PolicyDefinitionLoadManager.class);

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
    public boolean load(){
        logger.info(TraceUtils.getFormatTrace()+"PolicyDefinitionLoadManager start");
        long beginTime = System.currentTimeMillis();

        //取得合作方范围
        Set<String> partners = partnerClusterCache.getPartners();
        //TODO 排查问题，待删除
        logger.info("加载的合作方信息为：{}",partners.toString());

        //取得策略定义列表
        List<PolicyDefinition> PolicyModifiedDOList = policyDefinitionRepository.queryByPartners(partners);

        for(PolicyDefinition policyDefinition:PolicyModifiedDOList) {
            policyDefinitionCache.put(policyDefinition.getUuid(),policyDefinition);
        }

        logger.info(TraceUtils.getFormatTrace()+"PolicyDefinitionLoadManager success, cost:{}, size:{}",
                System.currentTimeMillis() - beginTime, PolicyModifiedDOList.size());
        return true;
    }
}
