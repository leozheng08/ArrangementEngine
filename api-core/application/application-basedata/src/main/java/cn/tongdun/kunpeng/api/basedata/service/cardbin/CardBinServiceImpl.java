package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    /**
     * 银行卡bin 类型
     */
//    private static final String dim_type = BANKCARD_ALL_BIN.getDimAndType();
//    private static final String dim_type = "bankcard_binall";

    @Value("${cardbin.aerospike.set:bankcard_binall_1}")
    private String asCardbinSet;

    @Autowired
    AerospikeServiceImpl aerospikeService;


    /**
     * 根据银行卡或者卡bin查询卡bin信息
     *
     * @param id    银行卡或者卡bin
     * @return      卡bin信息
     */
    @Override
    public CardBinTO getCardBinInfoById(String id) {
        // 入参校验: 校验卡号或者卡bin
        if (!checkCardBinAll(id)) {
            logger.error("输入参数校验失败：id=[{}]", id);
            return null;
        }

        // 目前卡号全是16位，如果不足16位，则认为传入的是卡bin
        boolean isCardNum = id.length() == CARD_PAN_LENGTH;

        // 获取路由信息
//        String asSet = SyncClientControlCacheManager.getSet(dim_type);
        String asSet = asCardbinSet;

        // 从as查询卡bin信息
        String cardBin;
        String line = "";
        if (isCardNum) {
            for (int len = CARD_BIN_MAX_LENGTH; len >= CARD_BIN_MIN_LENGTH; len--) {
                cardBin = id.substring(0, len);
                line = aerospikeService.get(asSet, cardBin);
                if (StringUtils.isNotEmpty(line)){
                    break;
                }
            }
        } else {
            cardBin = id;
            line = aerospikeService.get(asSet, cardBin);
        }

        if (StringUtils.isEmpty(line)) {
            logger.error("获取不到卡bin信息：id=[{}]", id);
            return null;
        }

        // 拼装结果
        CardBinAll cardBinAll = packageCardBinAll(line);
        return trans2to(cardBinAll);
    }

    /**
     * 结构转换
     *
     * @param cardBinAll
     * @return
     */
    private CardBinTO trans2to(CardBinAll cardBinAll) {
        CardBinTO cardBinTO = new CardBinTO();
        cardBinTO.setBin(cardBinAll.getBin());
        cardBinTO.setCardBrand(cardBinAll.getCardBrand());
        cardBinTO.setCardCategory(cardBinAll.getCardCategory());
        cardBinTO.setCardType(cardBinAll.getCardType());
        cardBinTO.setCountryName(cardBinAll.getCountryName());
        cardBinTO.setIsoA2(cardBinAll.getIsoA2());
        cardBinTO.setIsoA3(cardBinAll.getIsoA3());
        cardBinTO.setIsoName(cardBinAll.getIsoName());
        cardBinTO.setIsoNumber(cardBinAll.getIsoNumber());
        cardBinTO.setIssuingOrg(cardBinAll.getIssuingOrg());
        cardBinTO.setIssuingOrgPhone(cardBinAll.getIssuingOrgPhone());
        cardBinTO.setIssuingOrgWeb(cardBinAll.getIssuingOrgWeb());
        cardBinTO.setPanLength(cardBinAll.getPanLength());
        cardBinTO.setPurposeFlag(cardBinAll.getPurposeFlag());
        cardBinTO.setRegulated(cardBinAll.getRegulated());
        return cardBinTO;
    }

    /**
     * 根据查询到的结果组装返回结果
     * bin^card_brand^issuing_org^card_type^card_category^iso_name^iso_a2^iso_a3^iso_number^issuing_org_web^issuing_org_phone^pan_length^purpose_flag^regulated^countryname
     *
     * @param line  行数据
     * @return      返回结果
     */
    private CardBinAll packageCardBinAll(String line) {
        CardBinAll cardBinAll = new CardBinAll();

        String[] columns = line.split("\\^", -1);
        if (!columns[0].equals("")) {
            cardBinAll.setBin(Long.parseLong(columns[0]));
        }

        if (!columns[1].equals("")) {
            cardBinAll.setCardBrand(columns[1]);
        }

        if (!columns[2].equals("")) {
            cardBinAll.setIssuingOrg(columns[2]);
        }

        if (!columns[3].equals("")) {
            cardBinAll.setCardType(columns[3]);
        }

        if (!columns[4].equals("")) {
            cardBinAll.setCardCategory(columns[4]);
        }

        if (!columns[5].equals("")) {
            cardBinAll.setIsoName(columns[5]);
        }

        if (!columns[6].equals("")) {
            cardBinAll.setIsoA2(columns[6]);
        }

        if (!columns[7].equals("")) {
            cardBinAll.setIsoA3(columns[7]);
        }

        if (!columns[8].equals("")) {
            cardBinAll.setIsoNumber(Integer.parseInt(columns[8]));
        }

        if (!columns[9].equals("")) {
            cardBinAll.setIssuingOrgWeb(columns[9]);
        }

        if (!columns[10].equals("")) {
            cardBinAll.setIssuingOrgPhone(columns[10]);
        }

        if (!columns[11].equals("")) {
            cardBinAll.setPanLength(Integer.parseInt(columns[11]));
        }

        if (!columns[12].equals("")) {
            cardBinAll.setPurposeFlag(columns[12]);
        }

        if (!columns[13].equals("")) {
            cardBinAll.setRegulated(columns[13]);
        }

        if (!columns[14].equals("")) {
            cardBinAll.setCountryName(columns[14]);
        }

        return cardBinAll;
    }

    public static boolean checkCardBinAll(String bankcard) {
        return Pattern.matches(CARD_ALL_REGEX, bankcard);
    }
}