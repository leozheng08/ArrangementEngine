package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.RiskResponse;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 1100)
public class AssignFieldValueStep implements IRiskStep {

    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {


        return true;
    }
}
