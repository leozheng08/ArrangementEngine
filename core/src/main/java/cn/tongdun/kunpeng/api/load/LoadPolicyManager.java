package cn.tongdun.kunpeng.api.load;

import cn.tongdun.kunpeng.api.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.cluster.ClusterCache;
import cn.tongdun.kunpeng.api.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.dto.PolicyModifiedDTO;
import cn.tongdun.kunpeng.api.policy.IPolicyRepository;
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
import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_POLICY)
public class LoadPolicyManager implements ILoadByPartner{

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
    private ClusterCache clusterCache;



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

    /**
     * 加载当前集群下所有合作方的策略
     * @return
     */
    @Override
    public boolean load(){
        logger.info("PolicyLoadManager load()");

        //取得合作方范围
        Set<String> partners = clusterCache.getPartners();

        //取得策略列表
        List<PolicyModifiedDTO> PolicyModifiedDOList = policyRepository.queryByPartners(partners);


        List<LoadPolicyTask> tasks = new ArrayList<>();
        for(PolicyModifiedDTO policyModifiedDO:PolicyModifiedDOList){
            if(!policyModifiedDO.isStatus()){
                continue;
            }

            PolicyDTO policyDO = policyRepository.queryByUuid(policyModifiedDO.getPolicyUuid());

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


    /**
     * 只加载一个合作方的策略
     * @param partnerCode
     * @return
     */
    @Override
    public boolean loadByPartner(String partnerCode) {

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

        logger.info("PolicyLoadManager load() success");
        return true;
    }

}
