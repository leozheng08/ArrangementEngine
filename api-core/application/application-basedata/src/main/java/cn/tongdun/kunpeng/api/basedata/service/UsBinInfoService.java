package cn.tongdun.kunpeng.api.basedata.service;

//import cn.fraudmetrix.api.dubbo.FraudService;
//import cn.fraudmetrix.api.entity.CardBinTO;
//import cn.fraudmetrix.api.result.RiverResult;
import cn.fraudmetrix.module.riskbase.object.BinInfoDO;
import cn.tongdun.kunpeng.api.basedata.service.cardbin.CardBinService;
import cn.tongdun.kunpeng.api.basedata.service.cardbin.CardBinTO;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.Extension;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:43 下午
 */
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class UsBinInfoService implements BinInfoServiceExtPt{
    private static final Logger logger = LoggerFactory.getLogger(UsBinInfoService.class);

//    @Autowired
//    private FraudService fraudService;

    @Autowired
    private CardBinService cardBinService;

    @Autowired
    private IMetrics metrics;

    /**
     *  USBIN_OTHER_ERROR("80001", "查询cardbin数据异常"),
     *     USBIN_OTHER_DAT("80002", "查询不到cardbin信息");
     */
    static String USBIN_ERROR_OTHER="80001";
    static String USBIN_ERROR_DATA="80002";

    @Override
    public BinInfoDO getBinInfo(String binCode){
        return null;
    }

    @Override
    public boolean getBinInfo(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String cardBin = (String)context.get("cardBin");
        if (StringUtils.isNotBlank(cardBin)) {
            // 调用river获取CardBin信息
//            RiverResult<CardBinTO> result = null;
//            try {
//                result = fraudService.cardBin(cardBin);
//            } catch (Exception e) {
//                logger.error("调用river获取cardbin数据异常", e);
//            }
//            if (null != result && result.isSuccess()) {
//                setContext(context, result.getData());
//            }
            CardBinTO cardBinTO = null;
            try {
                cardBinTO = cardBinService.getCardBinInfoById(cardBin);
            }catch (Exception e){
                logger.error("查询cardbin数据异常", e);
                String[] tags = {
                        "sub_reason_code",USBIN_ERROR_OTHER};
                metrics.counter("kunpeng.api.subReasonCode",tags);
            }
            if(cardBinTO != null){
                setContext(context, cardBinTO);
            }else {
                logger.warn("查询不到cardbin信息");
                String[] tags = {
                        "sub_reason_code",USBIN_ERROR_DATA};
                metrics.counter("kunpeng.api.subReasonCode",tags);
            }
        }
        return true;
    }

    /**
     * 将查询到的卡bin信息复制给上线文中的系统参数值
     *
     * @param context       上下文
     * @param cardBinTO     卡bin信息
     */
    private void setContext(AbstractFraudContext context, CardBinTO cardBinTO) {
//        Map<String, String> elements = context.getElements();
//        elements.put("S_T_VB_CARDBIN", cardBinTO.getBin().toString());
//        setValue(elements, "S_T_VB_CARDBRAND", cardBinTO.getCardBrand());
//        setValue(elements, "S_T_VB_CARDTYPE", cardBinTO.getCardType());
//        setValue(elements, "S_T_VB_CARDCATEGORY", cardBinTO.getCardCategory());
//        setValue(elements, "S_T_VB_ISONAME", cardBinTO.getIsoName());
//        setValue(elements, "S_T_VB_ISOA2", cardBinTO.getIsoA2());
//        setValue(elements, "S_T_VB_ISOA3", cardBinTO.getIsoA3());
//        setValue(elements, "S_T_VB_ISSUINGORG", cardBinTO.getIssuingOrg());
//        setValue(elements, "S_T_VB_ISSUINGORGWEB", cardBinTO.getIssuingOrgWeb());
//        setValue(elements, "S_T_VB_PANLENGTH", cardBinTO.getPanLength().toString());
//        setValue(elements, "S_T_VB_PURPOSEFLAG", cardBinTO.getPurposeFlag());
//        setValue(elements, "S_T_VB_REGULATED", cardBinTO.getRegulated());
//        setValue(elements, "S_T_VB_COUNTRYNAME", cardBinTO.getCountryName());

//        setValue(context, "", cardBinTO.getBin().toString());
        setValue(context, "cardBrand", cardBinTO.getCardBrand());
        setValue(context, "cardType", cardBinTO.getCardType());
        setValue(context, "cardCategory", cardBinTO.getCardCategory());

//        setValue(context, "", cardBinTO.getIsoName());

        setValue(context, "isoa2", cardBinTO.getIsoA2());
//        setValue(context, "", cardBinTO.getIsoA3());
//        setValue(context, "", cardBinTO.getIssuingOrg());
//        setValue(context, "", cardBinTO.getIssuingOrgWeb());

//        setValue(context, "", cardBinTO.getPanLength().toString());
//        setValue(context, "", cardBinTO.getPurposeFlag());
//        setValue(context, "", cardBinTO.getRegulated());
        setValue(context, "cardBINCountry", cardBinTO.getCountryName());
        setValue(context, "cardBinCountry", cardBinTO.getCountryName());
    }

    private void setValue(AbstractFraudContext context, String key, Object value){
        if(value != null){
            context.set(key, value);
        }
    }


}
