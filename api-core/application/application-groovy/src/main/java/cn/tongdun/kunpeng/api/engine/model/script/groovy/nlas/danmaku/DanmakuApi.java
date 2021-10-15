package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.danmaku;

import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.text.TextAnalysServiceSPI;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.tfp.SampleSimilarServiceSPI;

/**
 * nlas服务 for Groovy
 *
 * @author wangrenjie
 * 2016-07-05
 */
public class DanmakuApi {

    public static DanmakuServiceSPI getInstance() {
        DanmakuServiceSPI result = (DanmakuServiceSPI) SpringContextHolder.getBean("danmakuServiceSPI");
        return result;
    }

    public static TextAnalysServiceSPI getTextAnalysServiceInstance() {
        TextAnalysServiceSPI result = (TextAnalysServiceSPI) SpringContextHolder.getBean("textAnalysServiceSPI");
        return result;
    }

    public static SampleSimilarServiceSPI getSampleSimilarServiceInstance() {
        SampleSimilarServiceSPI result = (SampleSimilarServiceSPI) SpringContextHolder.getBean("sampleSimilarServiceSPI");
        return result;
    }
}
