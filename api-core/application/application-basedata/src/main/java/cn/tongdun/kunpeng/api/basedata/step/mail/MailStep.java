package cn.tongdun.kunpeng.api.basedata.step.mail;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.dictionary.Dictionary;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.api.ruledetail.MailExceptionDetail;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuanhang
 *
 * @date 05/27/2020
 *
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.BASIC_DATA)
public class MailStep implements IRiskStep {

    @NotEmpty
    @Value("${mail.model.url}")
    String mailModelUrl;

    @Autowired
    DictionaryManager dictionaryManager;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        // 获取邮件模型地址
        context.getFieldValues().put("mail.model.url", mailModelUrl);
        // 获取数据库中租户绑定的key, 对应的邮件参数名称
        List<Dictionary> dictionaryList = dictionaryManager.getMailKey();
        if (CollectionUtils.isEmpty(dictionaryList)) {
            logger.warn("mail key dictionary empty, partnerCode :" + context.getPartnerCode());
            return true;
        }
        List<String> keyList = dictionaryList.stream().map(Dictionary::getKey).collect(Collectors.toList());
        context.getFieldValues().put("mails",keyList);
        return true;
    }
}
