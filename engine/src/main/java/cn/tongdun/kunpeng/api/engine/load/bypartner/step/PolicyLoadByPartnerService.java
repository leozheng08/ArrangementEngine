package cn.tongdun.kunpeng.api.engine.load.bypartner.step;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;
import cn.tongdun.kunpeng.api.engine.load.step.PolicyLoadTask;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class PolicyLoadByPartnerService {

    private Logger logger = LoggerFactory.getLogger(PolicyLoadByPartnerService.class);

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


    public boolean loadByPartner(String partnerCode){
        return loadByPartner(Sets.newHashSet(partnerCode));
    }

    public boolean loadByPartner(Set<String> partners){
        //取得策略列表
        List<PolicyModifiedDTO> policyModifiedDOList = policyRepository.queryDefaultPolicyByPartners(partners);

        List<PolicyLoadTask> tasks = new ArrayList<>();
        for(PolicyModifiedDTO policyModifiedDO:policyModifiedDOList){
            if(policyModifiedDO.getStatus() != 1){
                //todo 缓存不在用状态，便于返回404子码
                continue;
            }

            PolicyLoadTask task = new PolicyLoadTask(policyModifiedDO.getPolicyUuid(),policyRepository,defaultConvertorFactory,localCacheService);
            tasks.add(task);
        }

        try {
            executeThreadPool.invokeAll(tasks);
        } catch (Exception e) {
            logger.error("加载策略异常",  e);
            return false;
        }

        return true;
    }
}
