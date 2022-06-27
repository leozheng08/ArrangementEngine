package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.common.data.*;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.ParallelSubPolicy;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyManager;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import cn.tongdun.tdframework.core.util.TaskWrapLoader;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private PolicyCache policyCache;

    @Autowired
    private SubPolicyCache subPolicyCache;

    @Autowired
    private RuleCache ruleCache;

    @Autowired
    private SubPolicyManager subPolicyManager;

    @Autowired
    private IConfigRepository configRepository;

    private long configCreditTimeout = 0;
    private long configExecuteTimeout = 0;

    @Value("${parallelEngine.thread.core.size:64}")
    private int corePoolSize;

    @Value("${parallelEngine.thread.max.size:256}")
    private int maxPoolSize;

    @Value("${parallelEngine.thread.queue.size:20}")
    private int queueSize;


    @PostConstruct
    public void init() {
        this.executeThreadPool = threadService.createThreadPool(
                corePoolSize,
                maxPoolSize,
                30L,
                TimeUnit.MINUTES,
                queueSize,
                "ruleExecute");
        configExecuteTimeout = configRepository.getLongProperty("rule.engine.execute.timeout", DEFAULT_RULE_ENGINE_EXECUTE_TIMEOUT);
        configCreditTimeout = configRepository.getLongProperty("rule.engine.execute.credit.timeout", configExecuteTimeout);
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
        long timeout = configCreditTimeout;
        if (timeout < DEFAULT_RULE_ENGINE_EXECUTE_TIMEOUT) {
            timeout = DEFAULT_RULE_ENGINE_EXECUTE_TIMEOUT;
        }
        return timeout;
    }


    @Override
    public PolicyResponse execute(AbstractDecisionMode decisionMode, AbstractFraudContext context) {
        long start = System.currentTimeMillis();
        PolicyResponse policyResponse = new PolicyResponse();

        // 试运行的策略结果
        PolicyResponse tryPolicyResponse = new PolicyResponse();

        ParallelSubPolicy parallelSubPolicy = (ParallelSubPolicy) decisionMode;
        String policyUuid = parallelSubPolicy.getPolicyUuid();
        Policy policy = policyCache.get(policyUuid);

        //检查策略配置，如果策略配置有误则生成404子码
        if (!checkPolicyConfig(context, policy)) {
            return policyResponse;
        }

        policyResponse.setPolicyUuid(policy.getUuid());
        policyResponse.setPolicyName(policy.getName());

        // 本次调用为试运行调用
        if (context.isPilotRun()) {
            tryPolicyResponse.setPolicyUuid(policy.getUuid());
            tryPolicyResponse.setPolicyName(policy.getName());
        }

        context.setPolicyVersion(policy.getVersion());
        List<SubPolicy> subPolicyList = subPolicyCache.getSubPolicyByPolicyUuid(policyUuid);
        //过滤没有规则的子策略
        Iterator<SubPolicy> subPolicyListIterator = subPolicyList.iterator();
        while (subPolicyListIterator.hasNext()) {
            SubPolicy subPolicy = subPolicyListIterator.next();
            if (CollectionUtils.isEmpty(ruleCache.getRuleBySubPolicyUuid(subPolicy.getUuid()))) {
                subPolicyListIterator.remove();
            }
        }
        if (CollectionUtils.isEmpty(subPolicyList)) {
            logger.warn(TraceUtils.getFormatTrace() + "{},policyUuid:{}", ReasonCode.SUB_POLICY_NOT_EXIST.getCode(), policyUuid);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.SUB_POLICY_NOT_EXIST.getCode(), ReasonCode.SUB_POLICY_NOT_EXIST.getDescription(), "决策引擎执行"));
        }

        List<Callable<SubPolicyResponse>> tasks = new ArrayList<>();
        for (SubPolicy subPolicy : subPolicyList) {
            SubPolicyExecuteAsyncTask task = new SubPolicyExecuteAsyncTask(subPolicyManager, subPolicy.getUuid(), context);
            tasks.add(TaskWrapLoader.getTaskWrapper().wrap(task));
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
            logger.error(TraceUtils.getFormatTrace() + "规则引擎执行被中断", e);
        } catch (RejectedExecutionException e) {
            logger.error(TraceUtils.getFormatTrace() + "规则引擎执行被丢弃", e);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "规则引擎执行异常", e);
        }

        if (null == futures || futures.isEmpty()) {
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_ERROR.getCode(), ReasonCode.RULE_ENGINE_ERROR.getDescription(), "决策引擎执行"));
            // 规则引擎运行异常并且有试运行调用权限，将试运行策略结果放至上下文，防止后续的NPE
            if (context.isPilotRun()) {
                context.setTryPolicyResponse(tryPolicyResponse);
            }
            return policyResponse;
        }
        List<SubPolicyResponse> subPolicyResponseList = new ArrayList<>(futures.size());
        for (Future<SubPolicyResponse> future : futures) {
            if (future.isDone() && !future.isCancelled()) {
                try {
                    SubPolicyResponse subPolicyResponse = future.get();
                    if (logger.isDebugEnabled()) {
                        logger.debug(TraceUtils.getFormatTrace() + "seqId:{} subPolicyResponse:{} ", context.getSeqId(), JSON.toJSONString(subPolicyResponse));
                    }
                    subPolicyResponseList.add(subPolicyResponse);
                } catch (InterruptedException e) {
                    logger.error(TraceUtils.getFormatTrace() + "获取规则引擎执行结果被中断", e);
                } catch (ExecutionException e) {
                    logger.error(TraceUtils.getFormatTrace() + "获取规则引擎执行结果失败1", e);
                } catch (Throwable e) {
                    logger.error(TraceUtils.getFormatTrace() + "获取规则引擎执行结果失败2", e);
                }
            } else {
                logger.warn(TraceUtils.getFormatTrace() + "规则引擎执行服务被cancel");
            }
        }

        // 超时的任务，结果不会添加到subPolicyResponseList中
        if (subPolicyResponseList.size() < futures.size()) {
            logger.info(TraceUtils.getFormatTrace() + "subPolicyResponseList.size():{} futures.size():{}", subPolicyResponseList.size(), futures.size());
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_ENGINE_TIMEOUT.getCode(), ReasonCode.RULE_ENGINE_TIMEOUT.getDescription(), "决策引擎执行"));
            policyResponse.setCostTime(System.currentTimeMillis() - start);
            // 超时的情况下，试运行也需要进行添加，防止后面的NPE异常
            if (context.isPilotRun()) {
                tryPolicyResponse.setCostTime(System.currentTimeMillis() - start);
                context.setTryPolicyResponse(tryPolicyResponse);
            }
            return policyResponse;
        }
        fillPolicyResponse(policyResponse, subPolicyResponseList, start);

        // 如果是试运行调用
        if (context.isPilotRun()) {
            // 填充试运行的PolicyResponse
            fillTryPolicyResponse(tryPolicyResponse, subPolicyResponseList, start);
            // 设置到上下文中
            context.setTryPolicyResponse(tryPolicyResponse);
        }

        return policyResponse;
    }

    // 填充正式的PolicyResponse
    public void fillPolicyResponse(PolicyResponse policyResponse, List<SubPolicyResponse> subPolicyResponseList, long start) {
        policyResponse.setSuccess(true);

        // 过滤掉只含有试运行的子策略结果
        List<SubPolicyResponse> subPolicyResponses = getSubPolicyResponseList(subPolicyResponseList);
        policyResponse.setSubPolicyResponses(subPolicyResponses);

        // 获取最坏的策略结果
        SubPolicyResponse finalSubPolicyResponse = createFinalSubPolicyResult(subPolicyResponses);
        policyResponse.setFinalSubPolicyResponse(finalSubPolicyResponse);

        policyResponse.setDecision(finalSubPolicyResponse.getDecision());
        policyResponse.setScore(finalSubPolicyResponse.getScore());
        policyResponse.setRiskType(finalSubPolicyResponse.getRiskType());
        policyResponse.setCostTime(System.currentTimeMillis() - start);
    }

    // 填充试运行的PolicyResponse
    public void fillTryPolicyResponse(PolicyResponse policyResponse, List<SubPolicyResponse> subPolicyResponseList, long start) {
        policyResponse.setSuccess(true);
        policyResponse.setSubPolicyResponses(subPolicyResponseList);

        // 获取最坏的策略结果
        SubPolicyResponse finalSubPolicyResponse = createTryFinalSubPolicyResult(subPolicyResponseList);
        policyResponse.setFinalSubPolicyResponse(finalSubPolicyResponse);

        // 取子策略试运行的分数和结果
        policyResponse.setDecision(finalSubPolicyResponse.getTryDecision());
        policyResponse.setScore(finalSubPolicyResponse.getTryScore());
        policyResponse.setRiskType(finalSubPolicyResponse.getRiskType());
        policyResponse.setCostTime(System.currentTimeMillis() - start);
    }

    // 当前的子策略结果只含试运行的话，需要去除该子策略结果
    public List<SubPolicyResponse> getSubPolicyResponseList(List<SubPolicyResponse> subPolicyResponseList) {
        List<SubPolicyResponse> responseList = new ArrayList<>();
        for (SubPolicyResponse subPolicyResponse : subPolicyResponseList) {
            if (subPolicyResponse.getRuleResponses().size() != 0) {
                responseList.add(subPolicyResponse);
            }
        }
        return responseList;
    }


    //检查策略配置，生成404子码
    private boolean checkPolicyConfig(AbstractFraudContext context, Policy policy) {
        //取得此策略配置的子策略，子策略并行执行。
        String policyUuid = policy.getUuid();
        List<SubPolicy> subPolicyList = subPolicyCache.getSubPolicyByPolicyUuid(policyUuid);

        if (subPolicyList == null || subPolicyList.isEmpty()) {
            logger.warn(TraceUtils.getFormatTrace() + "{},policyUuid:{}", ReasonCode.SUB_POLICY_NOT_EXIST.toString(), policyUuid);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.SUB_POLICY_NOT_EXIST.getCode(), ReasonCode.SUB_POLICY_NOT_EXIST.getDescription(), "决策引擎执行"));
            return false;
        }

        int ruleCount = 0;
        for (SubPolicy subPolicy : subPolicyList) {
            String subPolicyUuid = subPolicy.getUuid();
            if (subPolicyUuid == null) {
                logger.warn(TraceUtils.getFormatTrace() + "{},policyUuid:{},subPolicyUuid:{}", ReasonCode.SUB_POLICY_LOAD_ERROR.toString(), policyUuid, subPolicyUuid);
                context.addSubReasonCode(new SubReasonCode(ReasonCode.SUB_POLICY_LOAD_ERROR.getCode(), ReasonCode.SUB_POLICY_LOAD_ERROR.getDescription(), "决策引擎执行"));
                return false;
            }
            if (subPolicy.getRuleUuidList() == null) {
                continue;
            }

            // ruleCount += subPolicy.getRuleUuidList().size();
            for (String ruleUuid : subPolicy.getRuleUuidList()) {
                Rule rule = ruleCache.get(ruleUuid);
                if (rule == null) {
                    logger.warn(TraceUtils.getFormatTrace() + "{},policyUuid:{},subPolicyUuid:{},ruleUuid:{}", ReasonCode.RULE_LOAD_ERROR.toString(), policyUuid, subPolicyUuid, ruleUuid);
                    context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_LOAD_ERROR.getCode(), ReasonCode.RULE_LOAD_ERROR.getDescription(), "决策引擎执行"));
                    return false;
                }
                if (!rule.isPilotRun()) {
                    ruleCount++;
                }
            }
        }
        /**
         * 如果当前规则只存在试运行的规则，则直接返回："40402""对应的策略下没有规则"
         */
        if (ruleCount == 0) {
            logger.warn(TraceUtils.getFormatTrace() + "{},policyUuid:{}", ReasonCode.RULE_NOT_EXIST.toString(), policyUuid);
            context.addSubReasonCode(new SubReasonCode(ReasonCode.RULE_NOT_EXIST.getCode(), ReasonCode.RULE_NOT_EXIST.getDescription(), "决策引擎执行"));
            return false;
        }
        return true;
    }


}
