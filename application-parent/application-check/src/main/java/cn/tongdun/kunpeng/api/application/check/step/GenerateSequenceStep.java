package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.check.util.GenerateSeqIdUtil;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.RequestParamName;
import cn.tongdun.kunpeng.common.data.RiskResponse;
import cn.tongdun.kunpeng.api.application.check.util.DesensitizeUtil;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

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
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {

        String seqId = request.get("seq_id");
        if (StringUtils.isBlank(seqId)) {
            seqId = GenerateSeqIdUtil.generateSeqId();
        }
        response.setSeq_id(seqId);
        context.setSequenceId(seqId);
        context.setRequestId(request.get("requestId"));

        try {
            String time = seqId.substring(0, 13);
            context.setEventOccurTime(new Date(Long.parseLong(time)));
        } catch (Exception e) {
            context.setEventOccurTime(new Date());
        }

        // secret_key脱敏后再打日志
        String originalSecretKey = request.get(RequestParamName.SECRET_KEY);
        request.put(RequestParamName.SECRET_KEY, DesensitizeUtil.secretKey(originalSecretKey));
        logger.info("REQ: seq_id: {}, request: {}", seqId, request);
        // 日志打完，恢复回客户的原始值，后面需要使用
        request.put(RequestParamName.SECRET_KEY, originalSecretKey);

        return true;
    }
}
