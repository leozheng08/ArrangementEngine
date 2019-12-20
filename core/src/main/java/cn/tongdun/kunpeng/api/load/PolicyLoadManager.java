package cn.tongdun.kunpeng.api.load;

import cn.tongdun.kunpeng.api.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.cluster.ClusterCache;
import cn.tongdun.kunpeng.api.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.dataobject.PolicyDO;
import cn.tongdun.kunpeng.api.dataobject.PolicyModifiedDO;
import cn.tongdun.kunpeng.api.engine.RuleField;
import cn.tongdun.kunpeng.api.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.policy.PolicyCache;
import cn.tongdun.kunpeng.api.rule.RuleCache;
import cn.tongdun.kunpeng.api.runmode.RunModeCache;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicyCache;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_POLICY)
public class PolicyLoadManager implements ILoad{

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    private ExecutorService executeThreadPool;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private IPolicyRepository policyRepository;

    @Autowired
    private DefaultConvertorFactory defaultConvertorFactory;

    @Autowired
    private LocalCacheService localCacheService;

    @Autowired
    private PolicyCache policyCache;
    @Autowired
    private SubPolicyCache subPolicyCache;
    @Autowired
    private RuleCache ruleCache;
    @Autowired
    ClusterCache clusterCache;
    @Autowired
    private RunModeCache runModeCache;


    @PostConstruct
    public void init() {
        this.executeThreadPool = threadService.createThreadPool(
                8,
                8,
                30L,
                TimeUnit.MINUTES,
                Integer.MAX_VALUE,
                "loadPolicy");
    }

    @Override
    public boolean load(){
        logger.info("PolicyLoadManager load()");

        //取得合作方范围
        Set<String> partners = clusterCache.getPartners();

        //取得策略列表
        List<PolicyModifiedDO> PolicyModifiedDOList = policyRepository.queryByPartners(partners);


        List<LoadPolicyTask> tasks = new ArrayList<>();
        for(PolicyModifiedDO policyModifiedDO:PolicyModifiedDOList){
            if(!policyModifiedDO.isStatus()){
                continue;
            }

            PolicyDO policyDO = policyRepository.queryByUuid(policyModifiedDO.getPolicyUuid());

            LoadPolicyTask task = new LoadPolicyTask(policyDO,defaultConvertorFactory,localCacheService);
            tasks.add(task);
        }

        try {
            executeThreadPool.invokeAll(tasks);
        } catch (Exception e) {
            logger.error("加载策略异常",  e);
            return false;
        }


        logger.info("PolicyLoadManager load() success");
        return true;
    }





}
