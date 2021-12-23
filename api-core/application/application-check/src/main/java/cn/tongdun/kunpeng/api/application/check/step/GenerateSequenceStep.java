package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.check.util.GenerateSeqIdUtil;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 从请求中获取seq_id,如果没有则生成seq_id
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 100)
public class GenerateSequenceStep implements IRiskStep {
    private Logger logger = LoggerFactory.getLogger(GenerateSequenceStep.class);

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        context.setPartnerCode(request.getPartnerCode());
        context.setAppName(request.getAppName());

        //seq_id
        String seqId = request.getSeqId();
        if (StringUtils.isBlank(seqId)) {
            seqId = GenerateSeqIdUtil.generateSeqId();
        }
        response.setSeqId(seqId);
        context.setSeqId(seqId);
        TraceUtils.setTrace(seqId);

        //requestId
        context.setRequestId(request.getRequestId());

        //事件发生时间，默认取seqId中的时间戳，如果客户有传event_occur_time则会覆盖
        try {
            String time = seqId.substring(0, 13);
            context.setEventOccurTime(new Date(Long.parseLong(time)));
        } catch (Exception e) {
            context.setEventOccurTime(new Date());
        }

//        logger.info(TraceUtils.getFormatTrace()+"REQ: seq_id: {}, request: {}", seqId, request);
        return true;
    }
}
