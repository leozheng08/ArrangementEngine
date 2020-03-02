package cn.tongdun.kunpeng.api.application.runengine.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 决策工具执行后继处理，预留。无具体实现
 * @Author: liang.chen
 * @Date: 2020/2/20 下午5:54
 */
@Component
@Step(pipeline = Risk.NAME,phase = Risk.RUN_ENGINE,order = 1100)
public class AfterHandleStep implements IRiskStep {

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, Map<String, String> request){
        response.setSuccess(true);
        return true;
    }

}
