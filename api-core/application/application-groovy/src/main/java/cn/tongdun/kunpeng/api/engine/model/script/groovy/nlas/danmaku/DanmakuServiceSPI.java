package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.danmaku;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

import java.util.Map;

/**
 * 弹幕规则远程接口
 *
 * @author wangrenjie
 */
public interface DanmakuServiceSPI {

    double calculateDanmakuPoint(AbstractFraudContext context, String value, String userName);

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @param userName
     * @return
     */
    double calculateDanmakuPoint(String value, String userName, String type);

    double calculateDanmakuPoint(AbstractFraudContext context, String value, String userName, String type);

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @param userName
     * @return
     */
    boolean includeNumber(String value, String userName);

    boolean includeNumber(AbstractFraudContext context, String value, String userName);

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @param userName
     * @return
     */
    int calculateConatactsPoint(String value, String userName);

    int calculateConatactsPoint(AbstractFraudContext context, String value, String userName);

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @return
     */
    int calculateNotionalWord(String value);

    int calculateNotionalWord(AbstractFraudContext context, String value);

    /**
     * 动态脚本修改完后 删除
     *
     * @param text
     * @return
     */
    String htmlParser(String text);

    String htmlParser(AbstractFraudContext context, String text);

    /**
     * 动态脚本修改完后 删除
     *
     * @param seqId
     * @param content
     * @param type
     * @param values
     * @return
     */
    String textFeature(String seqId, String content, String type, Map<String, String> values);

    String textFeature(AbstractFraudContext context, String seqId, String content, String type, Map<String, String> values);

}
