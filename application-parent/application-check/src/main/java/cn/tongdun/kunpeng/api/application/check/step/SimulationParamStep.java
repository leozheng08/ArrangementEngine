package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 将仿真调用的参数设置到上下文中
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 1200)
public class SimulationParamStep implements IRiskStep {

    private Logger logger = LoggerFactory.getLogger(SimulationParamStep.class);

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        //仿真专用
        writeSimulationArgs(context, request);

        return true;
    }


    /**
     * 将仿真调用的参数写入
     */
    private void writeSimulationArgs(AbstractFraudContext context,RiskRequest request) {
        if(StringUtils.isNotBlank(request.getSimulationUuid()) && StringUtils.equalsIgnoreCase(request.getSimulationUuid(), "1")) {
            context.setSimulationUuid(request.getSimulationUuid());
            if(!StringUtils.equalsIgnoreCase("all", request.getSimulationPartner())) {
                context.setSimulationPartner(request.getSimulationPartner());
                context.setSimulationApp(request.getSimulationApp());
            }

            if(StringUtils.isNotBlank(request.getTdSampleDataId())) {
                context.setTdSampleDataId(request.getTdSampleDataId());
            }
            context.setSimulationSeqId(request.getSimulationSeqId());
            context.setSimulation(true);
        }
    }
}
