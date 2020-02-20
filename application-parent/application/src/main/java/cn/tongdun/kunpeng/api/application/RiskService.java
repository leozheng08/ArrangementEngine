package cn.tongdun.kunpeng.api.application;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.infrastructure.config.ConfigManager;
import cn.tongdun.kunpeng.client.api.IRiskService;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.tdframework.common.dto.Response;
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
    PipelineExecutor pipelineExecutor;

    @Resource(name = "configManager")
    ConfigManager configManager;

    @Override
    public RiskResponse riskService(Map<String, String> request) {
        FraudContext context = new FraudContext();
        context.setRequestParamsMap(request);


        //测试
//        context.setPolicyUuid("123456789");
//        context.setPartnerCode(request.get("partner_code"));
//        context.set("accountMobile",request.get("accountMobile"));


        RiskResponse riskResponse = new RiskResponse();
        Response result = pipelineExecutor.execute(Risk.NAME, IRiskStep.class,
                step -> step.invoke(context, riskResponse, request), (isSuccess, e) ->
                {

                    //如果调用不成功时退出，不再执行后继步骤
                    return isSuccess == null || !isSuccess;
                }
        );

        return riskResponse;
    }
}
