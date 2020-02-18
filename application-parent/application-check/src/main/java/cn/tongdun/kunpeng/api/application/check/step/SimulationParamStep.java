package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.RiskResponse;
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
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {

        //仿真专用
        writeSimulationArgs(context, request);

        return true;
    }


    /**
     * 将仿真调用的参数写入
     */
    private void writeSimulationArgs(AbstractFraudContext context, Map<String, String> request) {
        if(StringUtils.isNotBlank(request.get("simulation_uuid")) && StringUtils.equalsIgnoreCase(request.get("td_td_simulation"), "1")) {
            context.set("simulationUuid", request.get("simulation_uuid"));
            if(!StringUtils.equalsIgnoreCase("all", request.get("simulation_partner"))) {
                context.setSimulatePartnerCode(request.get("simulation_partner"));
                context.setSimulateAppName(request.get("simulation_app"));
            }

            if(StringUtils.isNotBlank(request.get("td_sample_data_id"))) {
                context.set("td_sample_data_id", request.get("td_sample_data_id"));
            }
            context.setSimulateSequenceId(request.get("simulation_seqid"));
            context.setSimulation(true);
        }
    }
}
