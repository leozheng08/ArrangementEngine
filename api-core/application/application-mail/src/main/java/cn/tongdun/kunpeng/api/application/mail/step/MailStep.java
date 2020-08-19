package cn.tongdun.kunpeng.api.application.mail.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author yuanhang
 * @date 05/27/2020
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.BASIC_DATA)
public class MailStep implements IRiskStep {

    @Value("${mail.model.url}")
    String mailModelUrl;

    @Value("${mail.model.random.url}")
    String mailModelRandUrl;

    @Autowired
    RuleCache ruleCache;

    @Autowired
    PolicyCache policyCache;

    @Autowired
    SubPolicyCache subPolicyCache;

    @Autowired
    DictionaryManager dictionaryManager;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        // 获取邮件模型地址
        if (StringUtils.isEmpty(mailModelUrl) || StringUtils.isEmpty(mailModelRandUrl)) {
            logger.error(TraceUtils.getFormatTrace() + " mail model url empty, partnerCode :" + context.getPartnerCode());
            return true;
        }

        context.getFieldValues().put("mailModelUrl", mailModelUrl);
        context.getFieldValues().put("mailModelRandomUrl", mailModelRandUrl);

        return true;
    }
}
