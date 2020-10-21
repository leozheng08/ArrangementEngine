package cn.tongdun.kunpeng.api.basedata.service.cardbin;


import java.util.Map;

/**
 * 全球卡BIN服务
 *
 */
public interface CardBinService {
    /**
     * 根据银行卡或者卡bin查询卡bin信息
     * @param id    银行卡或者卡bin
     * @return      卡bin信息
     */
    CardBinTO getCardBinInfoById(String id);

    Map<String, Object> getRawCardBinInfo(String id);
}
