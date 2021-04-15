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
     * 返回历史取值 str
     */
    private String stringValue;
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

    /**
     * 指标编号
     */
    private Long indicatrixId;

    /**
     * 指标代码
     */
    private transient String meaningCode;

    /**
     * 一级行业分类中文
     */
    private transient String featureLevel1Name;

    /**
     * 二级行业分类中文
     */
    private transient String featureLevel2Name;

    /**
     * 三级行业分类中文
     */
    private transient String featureLevel3Name;

    /**
     * 指标结果对外状态码，监控告警使用
     */
    private transient int retCode = 200;

    /**
     * 指标结果详细状态码，快速定位问题
     */
    private transient int detailCode;
}
