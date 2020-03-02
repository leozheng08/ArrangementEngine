package cn.tongdun.kunpeng.api.application;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.application.step.ext.ICreateRiskResponseExtPt;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.kunpeng.common.data.ReasonCode;
import cn.tongdun.tdframework.common.dto.Response;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

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


    @Override
    public IRiskResponse riskService(Map<String, String> request) {
        FraudContext context = new FraudContext();
        context.setRequestParamsMap(request);
        BizScenario bizScenario = createBizScenario(context);
        context.setBizScenario(bizScenario);


        IRiskResponse riskResponse = null;

        try {
            riskResponse  = extensionExecutor.execute(ICreateRiskResponseExtPt.class,
                    bizScenario,
                    extension -> extension.createRiskResponse(context));

            //默认为无风险结果
            riskResponse.setFinalDecision(decisionResultTypeCache.getDefaultType().getCode());

            final IRiskResponse finalRiskResponse = riskResponse;
            Response result = pipelineExecutor.execute(Risk.NAME, IRiskStep.class,
                    step -> step.invoke(context, finalRiskResponse, request), (isSuccess, e) ->
                    {

                        //如果调用不成功时退出，不再执行后继步骤
                        return isSuccess != null && !isSuccess;
                    }
            );
        }catch (Exception e){
            logger.error("决策接口内部异常",e);
            riskResponse.setReasonCode(ReasonCode.INTERNAL_ERROR.toString());
        }

        return riskResponse;
    }

    private BizScenario createBizScenario(AbstractFraudContext context){
        BizScenario bizScenario = new BizScenario();
        bizScenario.setTenant(localEnvironment.getTenant());
        bizScenario.setPartner(context.getPartnerCode());
        return bizScenario;
    }
}
