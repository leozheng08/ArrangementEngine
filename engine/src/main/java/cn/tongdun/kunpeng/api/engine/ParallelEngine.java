package cn.tongdun.kunpeng.api.engine;


import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.ParallelSubPolicy;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionModeCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyManager;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;
import cn.tongdun.kunpeng.common.data.*;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import cn.tongdun.tdframework.core.concurrent.MDCUtil;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;

/**
 * 子策略并行执行引擎
 *
 * @Author: liang.chen
 * @Date: 2019/12/17 下午3:53
 */
@Component
public class ParallelEngine extends DecisionTool {

    private static Logger logger = LoggerFactory.getLogger(ParallelEngine.class);

    private static final long DEFAULT_RULE_ENGINE_EXECUTE_TIMEOUT = 800L;

    /**
     * ruleExecute 线程池
     */
    private ExecutorService executeThreadPool;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private DecisionModeCache decisionModeCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private SubPolicyManager subPolicyManager;

    @Resource(name = "dynamicConfigRepository")
    private IConfigRepository configRepository;


    @PostConstruct
    public void init() {
        this.executeThreadPool = threadService.createThreadPool(
                64,
                256,
                30L,
                TimeUnit.MINUTES,
                20,
                "ruleExecute");
    }


    @PreDestroy
    public void destroy() {
        if (null != this.executeThreadPool) {
            this.executeThreadPool.shutdown();
        }
    }

    /**
     * 根据事件类型确认规则引擎执行超时时间，单位ms 默认800ms
     *
     * @return 规则引擎执行超时时间
     */
    private long resolveExecuteTimeout(AbstractFraudContext context) {
        String eventType = context.getEventType();
        long timeout = configRepository.getLongConfigData("rule.engine.execute.credit.timeout",
                configRepository.getLongConfigData("rule.engine.execute.timeout", DEFAULT_RULE_ENGINE_EXECUTE_TIMEOUT));
        if (timeout < DEFAULT_RULE_ENGINE_EXECUTE_TIMEOUT) {
            timeout = DEFAULT_RULE_ENGINE_EXECUTE_TIMEOUT;
        }
        return timeout;
    }


    @Override
    public PolicyResponse execute(AbstractDecisionMode decisionMode, AbstractFraudContext context) {
        long start = System.currentTimeMillis();
        PolicyResponse policyResponse = new PolicyResponse();

        ParallelSubPolicy parallelSubPolicy = (ParallelSubPolicy) decisionMode;
        String policyUuid = parallelSubPolicy.getPolicyUuid();
        Policy policy = policyCache.get(policyUuid);

        policyResponse.setPolicyUuid(policy.getUuid());
        policyResponse.setPolicyName(policy.getName());


        //取得此策略配置的子策略，子策略并行执行。
        List<String> subPolicyUuidList = policy.getSubPolicyList();
        if(subPolicyUuidList.isEmpty()) {
            logger.warn("决策引擎执行40402,存在空的策略,policyUuid:{}", policyUuid);
            context.addSubReasonCode(new SubReasonCode("40402", "存在空的策略", "决策引擎执行"));
            policyResponse.setSuccess(false);
            return policyResponse;
        }

        List<Callable<SubPolicyResponse>> tasks = new ArrayList<>();
        for (String subPolicyUuid : subPolicyUuidList) {
            SubPolicyExecuteAsyncTask task = new SubPolicyExecuteAsyncTask(subPolicyManager, subPolicyUuid, context);
            tasks.add(MDCUtil.wrap(task));
        }

        //各子策略执行结果
        List<Future<SubPolicyResponse>> futures = null;
        try {
            if (context.isAsync()) {
                futures = executeThreadPool.invokeAll(tasks);
            } else {
                long timeout = resolveExecuteTimeout(context);
                futures = executeThreadPool.invokeAll(tasks, timeout, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException | NullPointerException e) {
            logger.error("规则引擎执行被中断", e);
        } catch (RejectedExecutionException e) {
            logger.error("规则引擎执行被丢弃", e);
        }

        if (null == futures || futures.isEmpty()) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_ERROR.getCode(), ReasonCode.RULE_ENGINE_ERROR.getDescription(), "决策引擎执行"));
            return policyResponse;
        }
        List<SubPolicyResponse> subPolicyResponseList = new ArrayList<>(futures.size());
        for (Future<SubPolicyResponse> future : futures) {
            if (future.isDone() && !future.isCancelled()) {
                try {
                    subPolicyResponseList.add(future.get());
                } catch (InterruptedException e) {
                    logger.error("获取规则引擎执行结果被中断", e);
                } catch (ExecutionException e) {
                    logger.error("获取规则引擎执行结果失败", e);
                }
            } else {
                logger.warn("规则引擎执行服务被cancel");
            }
        }

        // 超时的任务，结果不会添加到subPolicyResponseList中
        if (subPolicyResponseList.size() < futures.size()) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_TIMEOUT.getCode(), ReasonCode.RULE_ENGINE_TIMEOUT.getDescription(), "决策引擎执行"));
            policyResponse.setCostTime(System.currentTimeMillis() - start);
            return policyResponse;
        }

        policyResponse.setSuccess(true);
        policyResponse.setSubPolicyResponses(subPolicyResponseList);

        //取最坏策略结果
        SubPolicyResponse finalSubPolicyResponse = createFinalSubPolicyResult(subPolicyResponseList);
        policyResponse.setFinalSubPolicyResponse(finalSubPolicyResponse);

        policyResponse.setDecision(finalSubPolicyResponse.getDecision());
        policyResponse.setScore(finalSubPolicyResponse.getScore());
        policyResponse.setRiskType(finalSubPolicyResponse.getRiskType());
        policyResponse.setCostTime(System.currentTimeMillis() - start);
        return policyResponse;
    }




}
