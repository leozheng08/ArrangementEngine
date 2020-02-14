package cn.tongdun.kunpeng.api.engine.load.partner;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;
import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.load.LoadPolicyTask;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadByPartnerPipeline.NAME, phase = LoadByPartnerPipeline.LOAD_POLICY)
public class LoadPolicyByPartnerManager implements ILoadByPartner {

    private Logger logger = LoggerFactory.getLogger(LoadPolicyByPartnerManager.class);

    private ExecutorService executeThreadPool;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private IPolicyRepository policyRepository;

    @Autowired
    private DefaultConvertorFactory defaultConvertorFactory;

    @Autowired
    private LocalCacheService localCacheService;


    @PostConstruct
    public void init() {
        this.executeThreadPool = threadService.createThreadPool(
                4,
                4,
                30L,
                TimeUnit.MINUTES,
                Integer.MAX_VALUE,
                "loadPolicy");
    }



    /**
     * 只加载一个合作方的策略
     * @param partnerCode
     * @return
     */
    @Override
    public boolean loadByPartner(String partnerCode) {
        logger.info("LoadPolicyByPartnerManager loadByPartner()");
        //取得策略列表
        PolicyModifiedDTO policyModifiedDO = policyRepository.queryByPartner(partnerCode);

        if(!policyModifiedDO.isStatus()){
            return true;
        }

        PolicyDTO policyDO = policyRepository.queryByUuid(policyModifiedDO.getPolicyUuid());

        LoadPolicyTask task = new LoadPolicyTask(policyDO,defaultConvertorFactory,localCacheService);
        List<LoadPolicyTask> tasks = new ArrayList<>();
        tasks.add(task);

        try {
            executeThreadPool.invokeAll(tasks);
        } catch (Exception e) {
            logger.error("加载策略异常",  e);
            return false;
        }

        logger.info("LoadPolicyByPartnerManager loadByPartner() success");
        return true;
    }

}
