package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.text;

import cn.fraudmetrix.nlas.dubbo.service.text.TextAnalysService;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("textAnalysServiceSPI")
public class TextAnalysServiceSPIAdapter implements TextAnalysServiceSPI {

    private Logger logger = LoggerFactory.getLogger(TextAnalysServiceSPIAdapter.class);

    @Autowired
    private TextAnalysService textAnalysService;

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @return
     */
    @Deprecated
    @Override
    public Double textModelScore(String value) {
        try {
            return textAnalysService.textModelScore(value);
        } catch (Exception e) {
            logger.error("查询文本分析模型分数异常: value={}, e={}", value, e);
            return 0d;
        }
    }

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @return
     */
    @Deprecated
    @Override
    public Double anchorModelScore(String value) {
        try {
            return textAnalysService.anchorModelScore(value);
        } catch (Exception e) {
            logger.error("查询主播模型分数异常: value={}, e={}", value, e);
            return 0d;
        }
    }

    @Override
    public Double textModelScore(AbstractFraudContext context, String value) {
        try {
            return textAnalysService.textModelScore(value);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.error("查询文本分析模型分数异常: value={}, e={}", value, e);
            return 0d;
        }
    }

    @Override
    public Double anchorModelScore(AbstractFraudContext context, String value) {
        try {
            return textAnalysService.anchorModelScore(value);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("查询主播模型分数异常: value={}, e={}", value, e);
            return 0d;
        }
    }

}
