package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.tfp;

import cn.fraudmetrix.nlas.common.dto.SimilarityMatchResult;
import cn.fraudmetrix.nlas.dubbo.service.tfp.SampleSimilarService;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangrenjie on 16/12/14.
 */
@Service("sampleSimilarServiceSPI")
public class SampleSimilarServiceSPIAdapter implements SampleSimilarServiceSPI {

    private Logger logger = LoggerFactory.getLogger(SampleSimilarServiceSPIAdapter.class);

    @Autowired
    private SampleSimilarService sampleSimilarService;

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
    @Deprecated
    @Override
    public SimilarityMatchResult similarityMatch(String libraryCode, String partnerCode, String text, String isDev) {
        try {
            return sampleSimilarService.similarityMatch(libraryCode, partnerCode, text, isDev);
        } catch (Exception e) {
            logger.error("调用nlas相似度匹配异常: libraryCode={}, partnerCode={}, text={}, isDev={}, e={}", libraryCode, partnerCode, text, isDev, e);
            return null;
        }
    }

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
    @Deprecated
    @Override
    public double similarityMatchScore(String libraryCode, String partnerCode, String text, String isDev) {
        try {
            SimilarityMatchResult result = sampleSimilarService.similarityMatch(libraryCode, partnerCode, text, isDev);
            if (null != result) {
                return result.getSimilarityScore();
            } else {
                return 0d;
            }
        } catch (Exception e) {
            logger.error("调用nlas相似度匹配异常: libraryCode={}, partnerCode={}, text={}, isDev={}, e={}", libraryCode, partnerCode, text, isDev, e);
            return 0d;
        }
    }

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
    @Deprecated
    @Override
    public double similarityMatchScore(String libraryCode, String partnerCode, String text) {
        return this.similarityMatchScore(libraryCode, partnerCode, text, null);
    }

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
    @Deprecated
    @Override
    public boolean sampleImport(String libraryCode, String partnerCode, String text) {
        try {
            return sampleSimilarService.sampleImport(libraryCode, partnerCode, text);
        } catch (Exception e) {
            logger.error("调用nlas样本导入异常: libraryCode={}, partnerCode={}, text={}, e={}", libraryCode, partnerCode, text, e);
            return false;
        }
    }

    /**
     * 相似度匹配
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    @Override
    public SimilarityMatchResult similarityMatch(AbstractFraudContext context, String libraryCode, String partnerCode, String text, String isDev) {
        try {
            return sampleSimilarService.similarityMatch(libraryCode, partnerCode, text, isDev);
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            }
            logger.warn("调用nlas相似度匹配异常: libraryCode={}, partnerCode={}, text={}, e={}", libraryCode, partnerCode, text, e);
            return null;
        }
    }

    /**
     * 获取相似度
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @param isDev
     * @return
     */
    @Override
    public double similarityMatchScore(AbstractFraudContext context, String libraryCode, String partnerCode, String text, String isDev) {
        try {
            SimilarityMatchResult result = sampleSimilarService.similarityMatch(libraryCode, partnerCode, text, isDev);
            if (null != result) {
                return result.getSimilarityScore();
            } else {
                return 0d;
            }
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            }
            logger.warn("调用nlas相似度匹配异常: libraryCode={}, partnerCode={}, text={}, e={}", libraryCode, partnerCode, text, e);
            return 0d;
        }
    }

    /**
     * 获取相似度
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    @Override
    public double similarityMatchScore(AbstractFraudContext context, String libraryCode, String partnerCode, String text) {
        return this.similarityMatchScore(context, libraryCode, partnerCode, text, null);
    }

    /**
     * 样本导入
     *
     * @param libraryCode
     * @param partnerCode
     * @param text
     * @return
     */
    @Override
    public boolean sampleImport(AbstractFraudContext context, String libraryCode, String partnerCode, String text) {
        try {
            return sampleSimilarService.sampleImport(libraryCode, partnerCode, text);
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            }
            logger.warn("调用nlas样本导入异常: libraryCode={}, partnerCode={}, text={}, e={}", libraryCode, partnerCode, text, e);
            return false;
        }
    }


}
