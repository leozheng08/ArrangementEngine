package cn.tongdun.kunpeng.api.application.content.step.image;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.stereotype.Component;

/**
 * @description: 内容安全-图像识别
 * @author: zhongxiang.wang
 * @date: 2021-02-22 14:45
 */

@Component
@Step(pipeline = Risk.NAME, phase = Risk.RULE_DATA)
public class ImageStep implements IRiskStep {
    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        return true;
    }
}
