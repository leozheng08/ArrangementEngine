package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.creditcloud.api.APIResult;
import cn.fraudmetrix.creditcloud.entity.CardBinEntity;
import cn.tongdun.kunpeng.api.basedata.service.cardbin.CardBinService;
import cn.tongdun.kunpeng.api.basedata.service.cardbin.CardBinTO;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import cn.tongdun.tdframework.core.metrics.ITimeContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:43 下午
 */
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class UsBinInfoService implements BinInfoServiceExtPt {
    private static final Logger logger = LoggerFactory.getLogger(UsBinInfoService.class);

    @Autowired
    private CardBinService cardBinService;

    @Autowired
    private IMetrics metrics;

    @Autowired(required = false)
    cn.fraudmetrix.creditcloud.dubbo.CardBinService cardBinDubboService;

    @Autowired(required = false)
    private LucDynamicConfig lucDynamicConfig;

    private static final String TRUE = "1";

    @Override
    public boolean getBinInfo(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String cardBin = (String) context.get("cardBin");
        if (StringUtils.isNotBlank(cardBin)) {
            CardBinTO cardBinTO = null;
            try {
                if(StringUtils.equalsIgnoreCase(lucDynamicConfig.getCardBinInfoDubboSwitch(),TRUE)){
                    cardBinTO = getCardBinInfoFromDubbo(context,cardBin);
                }else{
                    cardBinTO = cardBinService.getCardBinInfoById(cardBin);
                }
            } catch (Exception e) {
                logger.error("查询cardbin数据异常", e);
                String[] tags = {
                        "sub_reason_code", ReasonCode.USBIN_ERROR_OTHER.getCode()};
                metrics.counter("kunpeng.api.subReasonCode", tags);
            }
            if (cardBinTO != null) {
                setContext(context, cardBinTO);
                request.getFieldValues().put("carBin_response", cardBinTO);
                logger.info("查询到seqId={}, cardBin={}", context.getSeqId(), cardBinTO);
            } else {
                logger.warn("查询不到cardbin信息 seqId :{}, cardBin={}", context.getSeqId(),cardBin);
            }
        }
        return true;
    }

    private CardBinTO getCardBinInfoFromDubbo(AbstractFraudContext contex,String id) {
        try{
            String[] tags = {
                    "dubbo_qps", "creditcloud.dubbo.CardBinService"};
            metrics.counter("kunpeng.api.dubbo.qps", tags);
            ITimeContext timeContext = metrics.metricTimer("kunpeng.api.dubbo.rt", tags);
            boolean maxPathMatch = true;
            APIResult<CardBinEntity> apiResult = cardBinDubboService.queryByBin(id, maxPathMatch);
            timeContext.stop();
            if(apiResult != null && apiResult.getSuccess()&& apiResult.getData() != null){
                return copyFromCardBin(apiResult.getData());
            }
        }catch (Exception e){
            if (ReasonCodeUtil.isTimeout(e)) {
                logger.error(TraceUtils.getFormatTrace() + "调用CardBin Dubbo服务超时: {}, seqId: {}", id, e,contex.getSeqId());
            } else {
                logger.error(TraceUtils.getFormatTrace() + "调用CardBin Dubbo服务异常: {}, seqId: {}", id, e,contex.getSeqId());
            }
        }
        return null;
    }

    private CardBinTO copyFromCardBin(CardBinEntity cardBinEntity){
        CardBinTO cardBinTo = new CardBinTO();
        cardBinTo.setCardBrand(cardBinEntity.getCardBrand());
        cardBinTo.setCardCategory(cardBinEntity.getCardCategory());
        cardBinTo.setBin(Long.valueOf(cardBinEntity.getCardNumber()));
        cardBinTo.setCardType(cardBinEntity.getCardType());
        cardBinTo.setCountryName(cardBinEntity.getCnName());
        cardBinTo.setIsoA2(cardBinEntity.getIsoA2());
        cardBinTo.setIsoA3(cardBinEntity.getIsoA3());
        cardBinTo.setIsoName(cardBinEntity.getIsoName());
        cardBinTo.setIssuingOrg(cardBinEntity.getIssuingOrg());
        cardBinTo.setIssuingOrgPhone(cardBinEntity.getIssuingOrgPhone());
        cardBinTo.setIssuingOrgWeb(cardBinEntity.getIssuingOrgWeb());
        cardBinTo.setPanLength(Integer.valueOf(cardBinEntity.getPanLength()));
        cardBinTo.setPurposeFlag(cardBinEntity.getPurposeFlag());
        cardBinTo.setRegulated(cardBinEntity.getRegulated());
        return cardBinTo;
    }

    /**
     * 将查询到的卡bin信息复制给上线文中的系统参数值
     *
     * @param context   上下文
     * @param cardBinTO 卡bin信息
     */
    private void setContext(AbstractFraudContext context, CardBinTO cardBinTO) {
        setValue(context, "cardBrand", cardBinTO.getCardBrand());
        setValue(context, "cardCategory", cardBinTO.getCardCategory());
        setValue(context,"bin",cardBinTO.getBin());
        setValue(context, "cardType", cardBinTO.getCardType());
        setValue(context, "cardBINCountry", cardBinTO.getCountryName());
        setValue(context, "cardBinCountry", cardBinTO.getCountryName());
        setValue(context, "isoa2", cardBinTO.getIsoA2());
        setValue(context, "isoa3", cardBinTO.getIsoA3());
        setValue(context, "isoName", cardBinTO.getIsoName());
        setValue(context, "issuingOrg", cardBinTO.getIssuingOrg());
        setValue(context, "issuingOrgPhone", cardBinTO.getIssuingOrgPhone());
        setValue(context, "issuingOrgWeb", cardBinTO.getIssuingOrgWeb());
        setValue(context, "panLength", cardBinTO.getPanLength());
        setValue(context, "purposeFlag", cardBinTO.getPurposeFlag());
        setValue(context,"regulated",cardBinTO.getRegulated());
    }

    private void setValue(AbstractFraudContext context, String key, Object value) {
        if (value != null) {
            context.set(key, value);
        }
    }


}
