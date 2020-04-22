package cn.tongdun.kunpeng.api.common.data;

import lombok.Data;

/**
 * @Author: liuq
 * @Date: 2020/2/20 3:16 PM
 */
@Data
public class PlatformIndexData {

    /**
     * 平台指标转换后的值
     */
    private Double value;
    /**
     * 平台指标原始值
     */
    private Double originalValue;
    /**
     * 平台指标描述
     */
    private String desc;
    /**
     * 指标详情，暂时写成Object，后面再强制转换为rule-detail中的详情
     */
    private Object detail;
}
