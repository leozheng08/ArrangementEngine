package cn.tongdun.kunpeng.api.engine.convertor.batch.evidence;

import cn.tongdun.kunpeng.api.engine.constant.Scope;
import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import lombok.Data;

@Data
public class EvidenceBatchRemoteCallData extends AbstractBatchRemoteCallData {

    /**
     * 匹配字段
     * eg：partnerCode
     */
    private String calcField;
    /**
     * 时间片
     *
     */
    private String timeslice;

    /**
     * 时间单位
     * 'y' 年
     */
    private String timeunit;

    /**
     * 不限时间
     *  'true' or 'false'
     */
    private String unlimitedTime;

    /**
     * 证据类型
     *  如：身份证等
     */
    private String evidenceType;

    /**
     * 风险类型
     *  如：creditCrack等
     */
    private String fraudtype;

    /**
     * 相似度阀值
     *  如：90  模糊匹配  当证据类型选择“家庭地址”或“工作单位地址”时有效
     */
    private String minimumSimilarity;

    /**
     * 证据来源
     * 如：全局
     */
    private String scope;

    /**
     * 是否匹配
     * any
     */
    private String iterateType;
}
