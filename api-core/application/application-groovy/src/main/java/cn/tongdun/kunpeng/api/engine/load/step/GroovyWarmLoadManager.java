package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScriptManager;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyContext;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.WrappedGroovyObject;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

/**
 * 脚本预热
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.warmup)
public class GroovyWarmLoadManager implements ILoad {

    private final static Logger logger = LoggerFactory.getLogger(GroovyWarmLoadManager.class);

    @Autowired
    private GroovyObjectCache groovyObjectCache;

    @Autowired
    private DynamicScriptManager dynamicScriptManager;

    @Override
    public boolean load() {
        logger.info(TraceUtils.getFormatTrace() + "GroovyWarmLoadManager start");
        long beginTime = System.currentTimeMillis();
        warmAllGroovyFields();
        logger.info(TraceUtils.getFormatTrace() + "GroovyWarmLoadManager success,cost:{}", System.currentTimeMillis() - beginTime);
        return true;

    }

    private void warmAllGroovyFields() {
        GroovyContext groovyContext = dynamicScriptManager.createGroovyContext();
        Collection<WrappedGroovyObject> groovyObjects = groovyObjectCache.getAll();
        for (WrappedGroovyObject groovyObject : groovyObjects) {
            warmGroovyField(groovyContext, groovyObject);
        }
    }

    private void warmGroovyField(GroovyContext groovyContext, WrappedGroovyObject wrappedGroovyObject) {
        try {
            FraudContext context = mockFraudContext();
            dynamicScriptManager.executeGroovyField(context, wrappedGroovyObject, groovyContext);
        } catch (Exception ex) {
            logger.error("动态脚本预热失败");
        }
    }

    private static FraudContext mockFraudContext() {
        FraudContext context = new FraudContext();
        context.setPartnerCode("demo");
        context.setAppName("test");
        context.setField("accountLogin", "helloworld");
        context.setField("accountMobile", "13712341234");
        context.setField("accountEmail", "hello@world.com");
        context.setEventId("login");
        context.setEventType("Login");
        context.setEventOccurTime(new Date());
        return context;
    }
}
