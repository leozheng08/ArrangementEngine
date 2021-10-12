package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.image;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by wangrenjie on 16/10/19.
 * 图像服务 for Groovy
 */
public class GleanerImageApi {

    public static GleanerImageService getInstance() {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        GleanerImageService result = (GleanerImageService) context.getBean("gleanerImageService");
        return result;
    }
}
