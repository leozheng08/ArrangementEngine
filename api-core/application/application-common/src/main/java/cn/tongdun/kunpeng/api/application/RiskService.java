package cn.tongdun.kunpeng.api.application;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.ext.ICreateRiskRequestExtPt;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.application.step.ext.ICreateRiskResponseExtPt;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.engine.model.compare.CompareInfo;
import cn.tongdun.kunpeng.api.engine.model.compare.ICompareInfoRepository;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinition;
import cn.tongdun.kunpeng.api.engine.model.policy.definition.PolicyDefinitionCache;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.json.JSON;
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

import java.util.Date;
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
    ICompareInfoRepository iCompareInfoRepository;
    @Autowired
    private PolicyDefinitionCache policyDefinitionCache;
    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Autowired
    private IMetrics metrics;


    @Override
    public IRiskResponse riskService(Map<String, Object> request) {

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

        String[] tags = {
                "partner_code",riskRequest.getPartnerCode()};
        ITimeContext timePartner = metrics.metricTimer("kunpeng.api.riskservice.partner.rt",tags);

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
        timeContext.stop();
        timePartner.stop();
        printCode(riskRequest,riskResponse);
        return riskResponse;

    }

    /**
     *  printCode
     *  riskRequest
     * @param riskResponse
     */
    private void  printCode(RiskRequest riskRequest,IRiskResponse riskResponse){
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
        /**
         * 按照合作方异常打点
         */
        if (Objects.nonNull(riskRequest.getPartnerCode())&&Objects.nonNull(riskResponse.getSubReasonCodes())){
            String[] tags = {
                    "partner_code",riskRequest.getPartnerCode()};
            metrics.counter("kunpeng.api.partner.code",tags);
        }
    }


    private BizScenario createBizScenario(Map<String, Object> request) {
        BizScenario bizScenario = new BizScenario();
        bizScenario.setTenant(localEnvironment.getTenant());
        bizScenario.setPartner(request.get("partnerCode") != null ? (String)request.get("partnerCode") : (String)request.get("partner_code"));
        if (StringUtils.isEmpty(bizScenario.getPartner())) {
            bizScenario.setPartner(request.get("PARTNERCODE") != null ? (String)request.get("PARTNERCODE") : null);
        }
        return bizScenario;
    }

    private CompareInfo buildCompareInfo(RiskRequest request, IRiskResponse riskResponse, FraudContext context) {
        CompareInfo compareInfo = new CompareInfo();
        compareInfo.setGmtCreate(new Date());
        compareInfo.setGmtModify(new Date());
        compareInfo.setSeqId(context.getSeqId());
        compareInfo.setEventOccurTime(context.getEventOccurTime());
        compareInfo.setPartnerCode(request.getPartnerCode());
        PolicyDefinition policyDefinition = policyDefinitionCache.getPolicyDefinition(request.getPartnerCode(), request.getAppName(), request.getEventId());
        compareInfo.setPolicyName(policyDefinition == null ? "" : policyDefinition.getName());
        compareInfo.setAppName(request.getAppName());
        compareInfo.setKpReponse(JSON.toJSONString(riskResponse));
        compareInfo.setKpRequest(JSON.toJSONString(request));
        return compareInfo;
    }


}
