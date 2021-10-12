package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.tfp;

import cn.fraudmetrix.nlas.common.dto.SimilarityMatchResult;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

/**
 * Created by wangrenjie on 16/12/14.
 */
public interface SampleSimilarServiceSPI {
    /**
     * 动态脚本修改完后 删除
     * <p>
     * 相似度匹配
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    SimilarityMatchResult similarityMatch(String libraryCode, String partnerCode, String text, String isDev);

    /**
     * 动态脚本修改完后 删除
     * <p>
     * 获取相似度
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @param isDev
     * @return
     */
    double similarityMatchScore(String libraryCode, String partnerCode, String text, String isDev);

    /**
     * 动态脚本修改完后 删除
     * <p>
     * 获取相似度
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    double similarityMatchScore(String libraryCode, String partnerCode, String text);

    /**
     * 动态脚本修改完后 删除
     * <p>
     * 样本导入
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    boolean sampleImport(String libraryCode, String partnerCode, String text);

    /**
     * 相似度匹配
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    SimilarityMatchResult similarityMatch(AbstractFraudContext context, String libraryCode, String partnerCode, String text, String isDev);

    /**
     * 获取相似度
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @param isDev
     * @return
     */
    double similarityMatchScore(AbstractFraudContext context, String libraryCode, String partnerCode, String text, String isDev);

    /**
     * 获取相似度
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    double similarityMatchScore(AbstractFraudContext context, String libraryCode, String partnerCode, String text);

    /**
     * 样本导入
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    boolean sampleImport(AbstractFraudContext context, String libraryCode, String partnerCode, String text);
}
