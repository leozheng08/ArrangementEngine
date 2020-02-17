package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.load.bypartner.step.PolicyLoadByPartnerService;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_POLICY, order = 200)
public class PolicyLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(PolicyLoadManager.class);

    @Autowired
    private PolicyLoadByPartnerService loadPolicyByPartnerService;

    @Autowired
    private PartnerClusterCache partnerClusterCache;

    /**
     * 加载当前集群下所有合作方的策略
     * @return
     */
    @Override
    public boolean load(){
        logger.info("PolicyLoadManager start");

        //取得合作方范围
        Set<String> partners = partnerClusterCache.getPartners();

        boolean success = loadPolicyByPartnerService.loadByPartner(partners);

        logger.info("PolicyLoadManager success:"+success);
        return success;
    }
}
