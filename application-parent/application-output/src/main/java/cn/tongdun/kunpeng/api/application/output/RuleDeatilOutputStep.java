package cn.tongdun.kunpeng.api.application.output;

import cn.tongdun.kunpeng.api.application.output.ext.IGeneralOutputExtPt;
import cn.tongdun.kunpeng.api.application.output.ext.IRuleDetailOutputExtPt;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 通用的输出, 填充返回结果到RiskResponse
 * @Author: liang.chen
 * @Date: 2020/2/20 下午5:54
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.OUTPUT,order = 1200)
public class RuleDeatilOutputStep implements IRiskStep {

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, Map<String, String> request){

        //通过通用输出扩展点
        return extensionExecutor.execute(IRuleDetailOutputExtPt.class,
                context.getBizScenario(),
                extension -> extension.ruleDetailOutput(context,response)
        );
    }

}
