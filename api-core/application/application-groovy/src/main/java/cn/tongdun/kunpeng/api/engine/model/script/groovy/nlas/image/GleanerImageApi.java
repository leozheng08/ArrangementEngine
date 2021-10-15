package cn.tongdun.kunpeng.api.engine.model.script.groovy.nlas.image;

import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;

/**
 * Created by wangrenjie on 16/10/19.
 * 图像服务 for Groovy
 */
public class GleanerImageApi {

    public static GleanerImageService getInstance() {
        GleanerImageService result = (GleanerImageService) SpringContextHolder.getBean("gleanerImageService");
        return result;
    }
}
