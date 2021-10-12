package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.danmaku;

import cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.text.TextAnalysServiceSPI;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.tfp.SampleSimilarServiceSPI;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * nlas服务 for Groovy
 *
 * @author wangrenjie
 * 2016-07-05
 */
public class DanmakuApi {

    public static DanmakuServiceSPI getInstance() {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        DanmakuServiceSPI result = (DanmakuServiceSPI) context.getBean("danmakuServiceSPI");
        return result;
    }

    public static TextAnalysServiceSPI getTextAnalysServiceInstance() {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        TextAnalysServiceSPI result = (TextAnalysServiceSPI) context.getBean("textAnalysServiceSPI");
        return result;
    }

    public static SampleSimilarServiceSPI getSampleSimilarServiceInstance() {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        SampleSimilarServiceSPI result = (SampleSimilarServiceSPI) context.getBean("sampleSimilarServiceSPI");
        return result;
    }
}
