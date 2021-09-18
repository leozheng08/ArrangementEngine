package cn.tongdun.kunpeng.api.application;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.ext.ICreateRiskRequestExtPt;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.application.step.ext.ICreateRiskResponseExtPt;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static cn.tongdun.kunpeng.api.common.MetricsConstant.*;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 下午5:16
 */
@Component
public class RiskService implements IRiskService {
    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);


    @Autowired
    private ILocalEnvironment localEnvironment;

    @Autowired
    private PipelineExecutor pipelineExecutor;

    @Autowired
    private DecisionResultTypeCache decisionResultTypeCache;

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Autowired
    private IMetrics metrics;

    @Override
    public IRiskResponse riskService(Map<String, Object> request, String bizName) {
        BizScenario bizScenario = createBizScenario(request);

        RiskRequest riskRequest = extensionExecutor.execute(ICreateRiskRequestExtPt.class,
                bizScenario,
                extension -> extension.createRiskRequest(request));


        return riskService(riskRequest, bizName);
    }

    @Override
    public IRiskResponse riskService(RiskRequest riskRequest, String bizName) {
        metrics.counter(METRICS_API_RISK_SERVICE_QPS_KEY);


        ITimeContext timeContext = metrics.metricTimer(METRICS_API_RISK_SERVICE_RT_KEY);
        if (StringUtils.isEmpty(riskRequest.getPartnerCode())) {
            riskRequest.setPartnerCode("NULL_partnerCode");
        }
        String[] tags = {
                METRICS_TAG_PARTNER_CODE, riskRequest.getPartnerCode()};
        metrics.counter(METRICS_API_RISK_SERVICE_PARTNER_QPS_KEY, tags);
        ITimeContext timePartner = metrics.metricTimer(METRICS_API_RISK_SERVICE_PARTNER_RT_KEY, tags);

        FraudContext context = new FraudContext();
        context.setRiskRequest(riskRequest);
        context.setRiskStartTime(System.currentTimeMillis());

        //business 依赖event_id找到对应的event_type再确认，放
        // 到GetPolicyUuidStep步骤中实现。
        String partnerCode = riskRequest.getPartnerCode();
        if (StringUtils.isNotEmpty(partnerCode) && "derica".equals(partnerCode)) {
            partnerCode = "globalegrow";
        }
        BizScenario bizScenario = BizScenario.valueOf(localEnvironment.getTenant(), BizScenario.DEFAULT, partnerCode);
        context.setBizScenario(bizScenario);

        IRiskResponse riskResponse = null;

        try {

            riskResponse = extensionExecutor.execute(ICreateRiskResponseExtPt.class,
                    bizScenario,
                    extension -> extension.createRiskResponse(context));

            //默认为无风险结果
            riskResponse.setFinalDecision(decisionResultTypeCache.getDefaultType().getCode());

            final IRiskResponse finalRiskResponse = riskResponse;
            Response result = pipelineExecutor.execute(bizName, IRiskStep.class,
                    step -> step.invoke(context, finalRiskResponse, riskRequest), (isSuccess, e) ->
                    {

                        //如果调用不成功时退出，不再执行后继步骤
                        return isSuccess != null && !isSuccess;
                    }
            );
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "决策接口内部异常", e);
            riskResponse.setReasonCode(ReasonCode.INTERNAL_ERROR.toString());
        } finally {
            ThreadContext.clear();
            TraceUtils.removeTrace();
        }
        timeContext.stop();
        timePartner.stop();
        printCode(riskRequest, riskResponse);
        return riskResponse;
    }

    @Override
    public IRiskResponse riskService(Map<String, Object> request) {
        return riskService(request, Risk.NAME);
    }


    @Override
    public IRiskResponse riskService(RiskRequest riskRequest) {
        return riskService(riskRequest, Risk.NAME);

    }

    /**
     * printCode
     * riskRequest
     *
     * @param riskResponse
     */
    private void printCode(RiskRequest riskRequest, IRiskResponse riskResponse) {
        /**
         * 按照合作方异常打点
         */
        if (Objects.nonNull(riskResponse.getReasonCode()) && Objects.nonNull(riskRequest.getPartnerCode())) {
            String[] tags = {
                    METRICS_TAG_REASON_CODE, riskResponse.getReasonCode(),
                    METRICS_TAG_PARTNER_CODE, riskRequest.getPartnerCode()
            };
            metrics.counter(METRICS_TAG_REASON_KEY, tags);
        }

        if (Objects.nonNull(riskResponse.getSubReasonCodes())) {
            String[] tags = {
                    METRICS_TAG_SUB_REASON_CODE, riskResponse.getSubReasonCodes(),
                    METRICS_TAG_PARTNER_CODE, riskRequest.getPartnerCode()};
            metrics.counter(METRICS_TAG_SUB_REASON_KEY, tags);
        }

        if (Objects.nonNull(riskRequest.getPartnerCode()) && Objects.nonNull(riskResponse.getSubReasonCodes())) {
            String[] tags = {
                    METRICS_TAG_SUB_REASON_CODE, riskResponse.getSubReasonCodes(),
                    METRICS_TAG_PARTNER_CODE, riskRequest.getPartnerCode()
            };
            metrics.counter(METRICS_TAG_PARTNER_KEY, tags);
        }
    }


    private BizScenario createBizScenario(Map<String, Object> request) {
        BizScenario bizScenario = new BizScenario();
        bizScenario.setTenant(localEnvironment.getTenant());
        bizScenario.setPartner(request.get("partnerCode") != null ? (String) request.get("partnerCode") : (String) request.get("partner_code"));
        if (StringUtils.isEmpty(bizScenario.getPartner())) {
            bizScenario.setPartner(request.get("PARTNERCODE") != null ? (String) request.get("PARTNERCODE") : null);
        }
        if (StringUtils.isNotEmpty(bizScenario.getPartner()) && "derica".equals(bizScenario.getPartner())) {
            bizScenario.setPartner("globalegrow");
        }
        return bizScenario;
    }

}
