package cn.tongdun.kunpeng.api.engine.load.bypartner.step;

import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;
import cn.tongdun.kunpeng.api.engine.load.step.PolicyLoadTask;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.IPlatformIndexRepository;
import cn.tongdun.kunpeng.api.engine.model.Indicatrix.PlatformIndexCache;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.constant.DeleteStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.engine.model.script.IPolicyScriptConfigRepository;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
    private PolicyCache policyCache;

    @Autowired
    private DefaultConvertorFactory defaultConvertorFactory;

    @Autowired
    private LocalCacheService localCacheService;

    @Autowired
    private PlatformIndexCache platformIndexCache;

    @Autowired
    private IPlatformIndexRepository platformIndexRepository;

    @Autowired
    private BatchRemoteCallDataCache batchRemoteCallDataCache;

    @Autowired
    private IPolicyScriptConfigRepository policyScriptConfigRepository;

    @Autowired
    private GroovyObjectCache groovyObjectCache;

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


    public boolean loadByPartner(String partnerCode) {
        return loadByPartner(Sets.newHashSet(partnerCode));
    }

    public boolean loadByPartner(Set<String> partners) {
        //取得默认策略列表
        List<PolicyModifiedDTO> policyList = policyRepository.queryDefaultPolicyByPartners(partners);

        //取得挑战者策略列表
        List<PolicyModifiedDTO> challengerPolicyList = policyRepository.queryChallengerPolicyByPartners(partners);
        if (challengerPolicyList != null && !challengerPolicyList.isEmpty()) {
            policyList.addAll(challengerPolicyList);
        }

        List<PolicyLoadTask> tasks = new ArrayList<>();
        for (PolicyModifiedDTO policyModifiedDO : policyList) {
            if (CommonStatusEnum.CLOSE.getCode() == policyModifiedDO.getStatus() ||
                    DeleteStatusEnum.INVALID.getCode() == policyModifiedDO.isDeleted()) {
                //缓存不在用状态，便于返回404子码
                Policy policy = convertor(policyModifiedDO);
                policyCache.put(policy.getUuid(), policy);
                continue;
            }

            PolicyLoadTask task = new PolicyLoadTask(policyModifiedDO.getUuid(), policyRepository, defaultConvertorFactory, localCacheService, platformIndexRepository, platformIndexCache, batchRemoteCallDataCache, policyScriptConfigRepository, groovyObjectCache);
            tasks.add(task);
        }

        try {
            executeThreadPool.invokeAll(tasks);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "加载策略异常", e);
            return false;
        }

        return true;
    }

    private Policy convertor(PolicyModifiedDTO policyModifiedDTO) {
        Policy policy = new Policy();
        BeanUtils.copyProperties(policyModifiedDTO, policy);
        return policy;
    }
}
