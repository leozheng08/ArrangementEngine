package cn.tongdun.kunpeng.api.application.mail.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.dictionary.Dictionary;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import cn.tongdun.kunpeng.api.application.mail.model.MailModelRule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuanhang
 * @date 05/27/2020
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.RULE_DATA, order = 2000)
public class MailStep implements IRiskStep {

    @Value("${mail.model.url}")
    String mailModelUrl;

    @Value("${mail.model.rand.url}")
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

        //该策略下所有子策略均无邮箱模型规则时，跳过后续
        String policyId = policyCache.getPolicyUuid(context.getPartnerCode(), context.getAppName(), context.getEventId(), context.getPolicyVersion());
        if (StringUtils.isNotEmpty(policyId)) {
            List<SubPolicy> subPolicies = subPolicyCache.getSubPolicyByPolicyUuid(policyId);
            if (CollectionUtils.isNotEmpty(subPolicies)) {
                if (subPolicies.stream().filter(r  -> ruleCache.get(r.getPolicyUuid()).getEval() instanceof MailModelRule).count() == 0){
                    logger.warn(TraceUtils.getFormatTrace() + "this policy contains no mailRule, skip filter, policyId :{}", policyId);
                    return true;
                }
            }
        }

        // 获取邮件模型地址
        if (StringUtils.isEmpty(mailModelUrl) || StringUtils.isEmpty(mailModelRandUrl)) {
            logger.warn("mail model url empty, partnerCode :" + context.getPartnerCode());
            return true;
        }
        context.getFieldValues().put("mailModelUrl", mailModelUrl);
        context.getFieldValues().put("mailModelRandUrl", mailModelRandUrl);

        // 获取数据库中租户绑定的key, 对应的邮件参数名称
        List<Dictionary> dictionaryList = dictionaryManager.getMailKey();
        if (CollectionUtils.isEmpty(dictionaryList)) {
            logger.warn("mail key dictionary empty, partnerCode :" + context.getPartnerCode());
            return true;
        }

        List<String> keyList = dictionaryList.stream().map(Dictionary::getKey).collect(Collectors.toList());
        context.getFieldValues().put("mails", keyList);

        return true;
    }
}
