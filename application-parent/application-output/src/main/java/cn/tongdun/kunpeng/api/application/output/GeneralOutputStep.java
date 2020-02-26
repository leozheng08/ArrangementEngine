package cn.tongdun.kunpeng.api.application.output;

import cn.tongdun.kunpeng.api.application.output.ext.IGeneralOutputExtPt;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultTypeCache;
import cn.tongdun.kunpeng.client.data.PolicyResult;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.PolicyResponse;
import cn.tongdun.kunpeng.common.data.SubPolicyResponse;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用的输出, 填充返回结果到RiskResponse
 * @Author: liang.chen
 * @Date: 2020/2/20 下午5:54
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.OUTPUT,order = 100)
public class GeneralOutputStep implements IRiskStep {

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request){

        //通过通用输出扩展点
        return extensionExecutor.execute(IGeneralOutputExtPt.class,
                context.getBizScenario(),
                extension -> extension.generalOutput(context,response)
        );
    }

}
