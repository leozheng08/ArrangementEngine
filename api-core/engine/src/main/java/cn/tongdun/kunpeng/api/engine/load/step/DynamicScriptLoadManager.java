package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyCompileManager;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_PARTNER)
public class DynamicScriptLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(DynamicScriptLoadManager.class);


    @Autowired
    IDynamicScriptRepository groovyRepository;

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    GroovyCompileManager groovyCompileManager;

    @Override
    public boolean load(){
        logger.info("DynamicScriptLoadManager start");
        long beginTime = System.currentTimeMillis();

        Set allPartners = new HashSet(partnerClusterCache.getPartners());
        allPartners.add("All");
        List<DynamicScript> scripts = groovyRepository.queryGroovyByPartners(allPartners);

        int failedCount = 0;
        for (DynamicScript script : scripts) {
            try {
                groovyCompileManager.addOrUpdate(script);
            } catch (Exception e) {
                failedCount++;
                logger.warn("Groovy编译失败,partnerCode:{},eventType:{},assignField:{},script:{},message:{}",
                        script.getPartnerCode(), script.getEventType(), script.getAssignField(),script.getScriptCode(),e.getMessage());
            }
        }

        int scriptsCount = 0;
        if(scripts != null){
            scriptsCount = scripts.size();
        }
        logger.info("DynamicScriptLoadManager success,cost:{},failedCount:{},scriptsCount:{}", System.currentTimeMillis() - beginTime,failedCount, scriptsCount);
        return true;
    }
}
