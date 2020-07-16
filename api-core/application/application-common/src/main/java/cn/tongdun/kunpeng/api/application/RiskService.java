package cn.tongdun.kunpeng.api.application;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.ext.ICreateRiskRequestExtPt;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.application.step.ext.ICreateRiskResponseExtPt;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

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
    public IRiskResponse riskService(Map<String, String> request) {

        BizScenario bizScenario = createBizScenario(request);

        RiskRequest riskRequest = extensionExecutor.execute(ICreateRiskRequestExtPt.class,
                bizScenario,
                extension -> extension.createRiskRequest(request));


        return riskService(riskRequest);
    }


    @Override
    public IRiskResponse riskService(RiskRequest riskRequest) {

        metrics.counter("kunpeng.api.riskservice.qps");

        ITimeContext timeContext = metrics.metricTimer("kunpeng.api.riskservice.rt");

        FraudContext context = new FraudContext();
        context.setRiskRequest(riskRequest);

        //business 依赖event_id找到对应的event_type再确认，放到GetPolicyUuidStep步骤中实现。
        BizScenario bizScenario = BizScenario.valueOf(localEnvironment.getTenant(), BizScenario.DEFAULT, riskRequest.getPartnerCode());
        context.setBizScenario(bizScenario);

        IRiskResponse riskResponse = null;

        try {

            riskResponse = extensionExecutor.execute(ICreateRiskResponseExtPt.class,
                    bizScenario,
                    extension -> extension.createRiskResponse(context));

            //默认为无风险结果
            riskResponse.setFinalDecision(decisionResultTypeCache.getDefaultType().getCode());

            final IRiskResponse finalRiskResponse = riskResponse;
            Response result = pipelineExecutor.execute(Risk.NAME, IRiskStep.class,
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
        printCode(riskResponse);
        return riskResponse;

    }

    /**
     *  printCode
     * @param riskResponse
     */
    private void  printCode(IRiskResponse riskResponse){
        if (Objects.nonNull(riskResponse.getReasonCode())){
            String[] tags = {
                    "reason_code",riskResponse.getReasonCode()};
            metrics.counter("kunpeng.api.reasonCode",tags);
        }
        if (Objects.nonNull(riskResponse.getSubReasonCodes())){
            String[] tags = {
                    "sub_reason_code",riskResponse.getSubReasonCodes()};
            metrics.counter("kunpeng.api.subReasonCode",tags);
        }
    }


    private BizScenario createBizScenario(Map<String, String> request) {
        BizScenario bizScenario = new BizScenario();
        bizScenario.setTenant(localEnvironment.getTenant());
        bizScenario.setPartner(request.get("partnerCode") != null ? request.get("partnerCode") : request.get("partner_code"));
        return bizScenario;
    }
}
