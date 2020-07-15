package cn.tongdun.kunpeng.api.basedata.step.cardbin;

import cn.fraudmetrix.api.entity.CardBinTO;
import cn.fraudmetrix.api.result.RiverResult;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.basedata.service.BinInfoServiceExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * cardBin信息获取
 *
 * @Author: liang.chen
 * @Date: 2020/2/10 下午2:19
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.BASIC_DATA)
public class CardBinStep implements IRiskStep {
    private static final Logger logger = LoggerFactory.getLogger(CardBinStep.class);

    @Autowired
    private ExtensionExecutor extensionExecutor;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String cardBin = (String)context.get("cardBin");
        if (StringUtils.isNotBlank(cardBin)) {
            // 调用river获取CardBin信息
            RiverResult<CardBinTO> result = null;
            try {
                extensionExecutor.execute(BinInfoServiceExtPt.class, context.getBizScenario(), extension -> extension.getBinInfo(context, response, request));
            } catch (Exception e) {
                logger.error("处理cardbin数据异常", e);
            }
        }
        return true;
    }
}
