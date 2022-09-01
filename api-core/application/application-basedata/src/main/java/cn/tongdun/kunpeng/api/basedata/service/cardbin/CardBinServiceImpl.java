package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import cn.fraudmetrix.creditcloud.api.APIResult;
import cn.fraudmetrix.creditcloud.entity.CardBinEntity;
import cn.tongdun.kunpeng.share.kv.IHashKVRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 根据银行卡或者卡bin查询卡bin信息
 *
 * @author lizh
 * @date 2019/7/16
 */
@Service
public class CardBinServiceImpl implements CardBinService {
    private static final Logger logger = LoggerFactory.getLogger(CardBinServiceImpl.class);

    /**
     * 全球卡bin数据的卡号长度都是16位
     */
    private static final int CARD_PAN_LENGTH = 16;

    /**
     * 卡bin的最小长度
     */
    private static final int CARD_BIN_MIN_LENGTH = 6;

    /**
     * 卡bin的最大长度
     */
    private static final int CARD_BIN_MAX_LENGTH = 11;

    private static final String CARD_ALL_REGEX = "^(\\d{6}|\\d{7}|\\d{8}|\\d{9}|\\d{10}|\\d{11}|\\d{16})$";

    private static final String SEMICOLON = ";";

    @Autowired
    IHashKVRepository redisHashKVRepository;

    @Autowired
    CardbinConfig cardbinConfig;

    @Autowired
    cn.fraudmetrix.creditcloud.dubbo.CardBinService cardBinService;

    private CardBinTO getCardBinInfoFromRedis(String id) {
        boolean maxPathMatch = true;
        APIResult<CardBinEntity> apiResult = cardBinService.queryByBin(id, maxPathMatch);
        CardBinTO cardBinTo = new CardBinTO();
        cardBinTo.setBin(Long.valueOf(apiResult.getData().getCardNumber()));
        //TODO 返回结果字段需要补齐
        return cardBinTo;
    }

    //private CardBinTO getCardBinInfoFromRedis(String id) {
    //    // 入参校验: 校验卡号或者卡bin
    //    if (!checkCardBinAll(id)) {
    //        logger.error("输入参数校验失败：id=[{}]", id);
    //        return null;
    //    }
    //    String ns = cardbinConfig.getNameSpace();
    //    String value = null;
    //    String cardBin = id;
    //    // 目前卡号全是16位，如果不足16位，则认为传入的是卡bin
    //    boolean isCardNum = id.length() == CARD_PAN_LENGTH;
    //    if (isCardNum) {
    //        for (int len = CARD_BIN_MAX_LENGTH; len >= CARD_BIN_MIN_LENGTH; len--) {
    //            cardBin = id.substring(0, len);
    //            value = redisHashKVRepository.hget(ns, cardBin);
    //            if (StringUtils.isNotEmpty(value)) {
    //                break;
    //            }
    //        }
    //    } else {
    //        value = redisHashKVRepository.hget(ns, cardBin);
    //        //8位卡bin查不到再查下前6位
    //        if (null == value && id.length() == 8) {
    //            cardBin = id.substring(0, 6);
    //            value = redisHashKVRepository.hget(ns, cardBin);
    //        }
    //    }
    //    if (value != null) {
    //        String[] columns = value.split(SEMICOLON);
    //        CardBinTO cardBinTO = new CardBinTO();
    //
    //        if (!columns[0].equals("")) {
    //            cardBinTO.setBin(Long.parseLong(columns[0]));
    //        }
    //        if (!columns[1].equals("")) {
    //            cardBinTO.setCardBrand(columns[1]);
    //        }
    //
    //        if (!columns[2].equals("")) {
    //            cardBinTO.setIssuingOrg(columns[2]);
    //        }
    //
    //        if (!columns[3].equals("")) {
    //            cardBinTO.setCardType(columns[3]);
    //        }
    //
    //        if (!columns[4].equals("")) {
    //            cardBinTO.setCardCategory(columns[4]);
    //        }
    //
    //        if (!columns[5].equals("")) {
    //            cardBinTO.setIsoName(columns[5]);
    //        }
    //
    //        if (!columns[6].equals("")) {
    //            cardBinTO.setIsoA2(columns[6]);
    //        }
    //
    //        if (!columns[7].equals("")) {
    //            cardBinTO.setIsoA3(columns[7]);
    //        }
    //
    //        if (!columns[8].equals("")) {
    //            cardBinTO.setIsoNumber(Integer.parseInt(columns[8]));
    //        }
    //
    //        if (!columns[9].equals("")) {
    //            cardBinTO.setIssuingOrgWeb(columns[9]);
    //        }
    //
    //        if (!columns[10].equals("")) {
    //            cardBinTO.setIssuingOrgPhone(columns[10]);
    //        }
    //
    //        if (!columns[11].equals("")) {
    //            cardBinTO.setPanLength(Integer.parseInt(columns[11]));
    //        }
    //
    //        if (!columns[12].equals("")) {
    //            cardBinTO.setPurposeFlag(columns[12]);
    //        }
    //
    //        if (!columns[13].equals("")) {
    //            cardBinTO.setRegulated(columns[13]);
    //        }
    //
    //        if (!columns[14].equals("")) {
    //            cardBinTO.setCountryName(columns[14]);
    //        }
    //
    //        return cardBinTO;
    //    } else {
    //        return null;
    //    }
    //}

    /**
     * is
     * 根据银行卡或者卡bin查询卡bin信息
     *
     * @param id 银行卡或者卡bin
     * @return 卡bin信息
     */
    @Override
    public CardBinTO getCardBinInfoById(String id) {
        return getCardBinInfoFromRedis(id);
    }

    @Override
    public Map<String, Object> getRawCardBinInfo(String id) {
        return getRawCardBinInfoFromRedis(id);
    }

    public Map<String, Object> getRawCardBinInfoFromRedis(String id) {
        Map<String, Object> resultMap = new HashMap<>(8);
        resultMap.put("id", id);
        // 入参校验: 校验卡号或者卡bin
        if (!checkCardBinAll(id)) {
            logger.error("输入参数校验失败：id=[{}]", id);
            resultMap.put("error_msg", String.format("输入参数校验失败：id=%s", id));
            return resultMap;
        }

        // 入参校验: 校验卡号或者卡bin
        if (!checkCardBinAll(id)) {
            logger.error("输入参数校验失败：id=[{}]", id);
            return null;
        }
        String ns = cardbinConfig.getNameSpace();
        resultMap.put("ns", ns);
        String value = null;
        String cardBin = id;
        // 目前卡号全是16位，如果不足16位，则认为传入的是卡bin
        boolean isCardNum = id.length() == CARD_PAN_LENGTH;
        if (isCardNum) {
            for (int len = CARD_BIN_MAX_LENGTH; len >= CARD_BIN_MIN_LENGTH; len--) {
                cardBin = id.substring(0, len);
                value = redisHashKVRepository.hget(ns, cardBin);
                if (StringUtils.isNotEmpty(value)) {
                    break;
                }
            }
        } else {
            value = redisHashKVRepository.hget(ns, cardBin);
        }
        resultMap.put("value", value);
        if (value != null) {
            String[] columns = value.split(SEMICOLON);
            CardBinTO cardBinTO = new CardBinTO();

            if (!columns[0].equals("")) {
                cardBinTO.setBin(Long.parseLong(columns[0]));
            }
            if (!columns[1].equals("")) {
                cardBinTO.setCardBrand(columns[1]);
            }

            if (!columns[2].equals("")) {
                cardBinTO.setIssuingOrg(columns[2]);
            }

            if (!columns[3].equals("")) {
                cardBinTO.setCardType(columns[3]);
            }

            if (!columns[4].equals("")) {
                cardBinTO.setCardCategory(columns[4]);
            }

            if (!columns[5].equals("")) {
                cardBinTO.setIsoName(columns[5]);
            }

            if (!columns[6].equals("")) {
                cardBinTO.setIsoA2(columns[6]);
            }

            if (!columns[7].equals("")) {
                cardBinTO.setIsoA3(columns[7]);
            }

            if (!columns[8].equals("")) {
                cardBinTO.setIsoNumber(Integer.parseInt(columns[8]));
            }

            if (!columns[9].equals("")) {
                cardBinTO.setIssuingOrgWeb(columns[9]);
            }

            if (!columns[10].equals("")) {
                cardBinTO.setIssuingOrgPhone(columns[10]);
            }

            if (!columns[11].equals("")) {
                cardBinTO.setPanLength(Integer.parseInt(columns[11]));
            }

            if (!columns[12].equals("")) {
                cardBinTO.setPurposeFlag(columns[12]);
            }

            if (!columns[13].equals("")) {
                cardBinTO.setRegulated(columns[13]);
            }

            if (!columns[14].equals("")) {
                cardBinTO.setCountryName(columns[14]);
            }
            resultMap.put("cardBinTO", cardBinTO);
        } else {
            resultMap.put("cardBinTO", null);
        }
        return resultMap;
    }


    public static boolean checkCardBinAll(String bankcard) {
        return Pattern.matches(CARD_ALL_REGEX, bankcard);
    }
}
