package cn.tongdun.kunpeng.api.application.phone.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.dictionary.Dictionary;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.api.engine.model.rule.RuleCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicyCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Step(pipeline = Risk.NAME, phase = Risk.BASIC_DATA)
public class PhoneStep implements IRiskStep {


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
        // 获取数据库中租户绑定的key, 对应的邮件参数名称
        List<Dictionary> dictionaryList = dictionaryManager.getPhoneSwitchKey();
        if (CollectionUtils.isEmpty(dictionaryList)) {
            logger.warn("mail key dictionary empty, partnerCode :" + context.getPartnerCode());
            return true;
        }
        return true;
    }
}