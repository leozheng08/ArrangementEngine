package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.danmaku;

import cn.fraudmetrix.nlas.dubbo.service.danmaku.DanmakuService;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("danmakuServiceSPI")
public class DanmakuServiceSPIAdapter implements DanmakuServiceSPI {

    private Logger logger = LoggerFactory.getLogger(DanmakuServiceSPIAdapter.class);

    @Autowired
    private DanmakuService danmakuService;

    /**
     * 动态脚本修改完后 删除
     *
     * @param text
     * @return
     */
    @Deprecated
    @Override
    public String htmlParser(String text) {
        try {
            return danmakuService.htmlParser(text);
        } catch (Exception e) {
            logger.error("调用nlas处理文本异常", e);
            return text;
        }
    }

    @Override
    public String htmlParser(AbstractFraudContext context, String text) {
        try {
            return danmakuService.htmlParser(text);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("调用nlas处理文本异常", e);
            return text;
        }
    }

    @Override
    public double calculateDanmakuPoint(AbstractFraudContext context, String value, String userName) {
        try {
            return danmakuService.calculateDanmakuPoint(value, userName);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("调用nlas计算弹幕内容的风险分数异常, userName={}, e={}", userName, e);
            return 0;
        }
    }

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @param userName
     * @param type
     * @return
     */
    @Deprecated
    @Override
    public double calculateDanmakuPoint(String value, String userName, String type) {
        try {
            return danmakuService.calculateDanmakuPoint(value, userName, type);
        } catch (Exception e) {
            logger.error("调用nlas计算长文本内容的风险分数异常, userName={}, e={}", userName, e);
            return 0;
        }
    }

    @Override
    public double calculateDanmakuPoint(AbstractFraudContext context, String value, String userName, String type) {
        try {
            return danmakuService.calculateDanmakuPoint(value, userName, type);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("调用nlas计算长文本内容的风险分数异常, userName={}, e={}", userName, e);
            return 0;
        }
    }

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @param userName
     * @return
     */
    @Deprecated
    @Override
    public boolean includeNumber(String value, String userName) {
        try {
            return danmakuService.includeNumber(value, userName);
        } catch (Exception e) {
            logger.error("调用nlas判断一段文本中是否包含qq、微信号异常, userName={}, e={}", userName, e);
            return false;
        }
    }

    @Override
    public boolean includeNumber(AbstractFraudContext context, String value, String userName) {
        try {
            return danmakuService.includeNumber(value, userName);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("调用nlas判断一段文本中是否包含qq、微信号异常, value={}, userName={}, e={}", value, userName, e);
            return false;
        }
    }

    /**
     * 动态脚本修改完后 删除
     *
     * @param value
     * @param userName
     * @return
     */
    @Deprecated
    @Override
    public int calculateConatactsPoint(String value, String userName) {
        try {
            return danmakuService.calculateConatactsPoint(value, userName);
        } catch (Exception e) {
            logger.error("调用nlas包含微信qq号关键词风险分数异常, value={}, userName={}, e={}", value, userName, e);
            return 0;
        }
    }

    @Override
    public int calculateConatactsPoint(AbstractFraudContext context, String value, String userName) {
        try {
            return danmakuService.calculateConatactsPoint(value, userName);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("调用nlas包含微信qq号关键词风险分数异常, value={}, userName={}, e={}", value, userName, e);
            return 0;
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
    public int calculateNotionalWord(String value) {
        try {
            return danmakuService.calculateNotionalWord(value);
        } catch (Exception e) {
            logger.error("调用nlas计算一短文本中实词个数异常, value={}, e={}", value, e);
            return -1;
        }
    }

    @Override
    public int calculateNotionalWord(AbstractFraudContext context, String value) {
        try {
            return danmakuService.calculateNotionalWord(value);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("调用nlas计算一短文本中实词个数异常, value={}, e={}", value, e);
            return -1;
        }
    }

    /**
     * 动态脚本修改完后 删除
     *
     * @param seqId
     * @param content
     * @param type
     * @param values
     * @return
     */
    @Deprecated
    @Override
    public String textFeature(String seqId, String content, String type, Map<String, String> values) {
        try {
            return danmakuService.textFeature(content, type, values);
        } catch (Exception e) {
            logger.error("调用nlas查询文本特征异常, seqId={}, type={}, values={}, e={}", seqId, type, values, e);
            return "";
        }
    }

    @Override
    public String textFeature(AbstractFraudContext context, String seqId, String content, String type, Map<String, String> values) {
        try {
            return danmakuService.textFeature(content, type, values);
        } catch (Exception e) {
            ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
            logger.warn("调用nlas查询文本特征异常, seqId={}, type={}, values={}, e={}", seqId, type, values, e);
            return "";
        }
    }
}
