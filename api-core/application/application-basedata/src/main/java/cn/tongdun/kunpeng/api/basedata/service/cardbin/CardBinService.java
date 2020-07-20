package cn.tongdun.kunpeng.api.basedata.service.cardbin;


/**
 * 全球卡BIN服务
 * 返回参数格式不同，使用新的接口
 *
 * @author lizh
 * @date 2019/7/16
 */
public interface CardBinService {
    /**
     * 根据银行卡或者卡bin查询卡bin信息
     * @param id    银行卡或者卡bin
     * @return      卡bin信息
     */
    CardBinTO getCardBinInfoById(String id);
}
