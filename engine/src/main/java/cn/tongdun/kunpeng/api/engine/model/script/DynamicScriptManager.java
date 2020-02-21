package cn.tongdun.kunpeng.api.engine.model.script;

import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.WrappedGroovyObject;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.*;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import cn.tongdun.tdframework.core.concurrent.MDCUtil;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 * @Author: liang.chen
 * @Date: 2019/12/16 下午7:58
 */
@Component
public class DynamicScriptManager {
    private final static Logger logger = LoggerFactory.getLogger(DynamicScriptManager.class);

    private ExecutorService executeThreadPool;

    @Autowired
    private GroovyObjectCache groovyObjectCache;

    @Autowired
    private ThreadService threadService;

    @PostConstruct
    public void init() {
        this.executeThreadPool = threadService.createThreadPool(
                32,
                100,
                30L,
                TimeUnit.MINUTES,
                Integer.MAX_VALUE,
                "groovyHandler-");
    }

    public boolean execute(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {
        try {
            handleField(context);
        } catch (Exception e) {
            logger.error("动态脚本调用异常", e);
        }
        return true;
    }


    private void handleField(AbstractFraudContext context) {
        List<Map<String,WrappedGroovyObject>> groovyObjectMaps = new ArrayList<Map<String,WrappedGroovyObject>>(4);
        String classKey = null;
        Map<String,WrappedGroovyObject> map = null;
        // 合作方指定事件类型
        classKey = context.getPartnerCode() + context.getAppName() + context.getEventType();
        map = groovyObjectCache.get(classKey);
        if (null != map) {
            groovyObjectMaps.add(map);
        }
        // 合作方全部事件类型
        classKey = context.getPartnerCode() + context.getAppName() + "All";
        map = groovyObjectCache.get(classKey);
        if (null != map) {
            groovyObjectMaps.add(map);
        }
        // 全局指定事件类型
        classKey = "All" + "All" + context.getEventType();
        map = groovyObjectCache.get(classKey);
        if (null != map) {
            groovyObjectMaps.add(map);
        }
        // 全局全部事件类型
        classKey = "All" + "All" + "All";
        map = groovyObjectCache.get(classKey);
        if (null != map) {
            groovyObjectMaps.add(map);
        }
        if (0 == groovyObjectMaps.size()) {
            return;
        }

        List<Callable<Boolean>> tasks = new ArrayList(groovyObjectMaps.size());
        for (Map<String,WrappedGroovyObject> objectMap : groovyObjectMaps) {
            tasks.add(MDCUtil.wrap(new GroovyRunTask(context, objectMap)));
        }
        List<Future<Boolean>> futures = null;
        long t1 = System.currentTimeMillis();
        try {
            futures = executeThreadPool.invokeAll(tasks);
        } catch (InterruptedException | NullPointerException e) {
            logger.warn("动态脚本执行出错", e);
        } catch (RejectedExecutionException e) {
            logger.error("动态脚本丢弃执行", e);
        }
        long t2 = System.currentTimeMillis();

        if(t2 - t1 > 30){
            logger.warn("动态脚本执行时间太长, groovy_execute_too_long costTime : {}", t2-t1);
        }

        List<Boolean> results = new ArrayList(groovyObjectMaps.size());
        if (null != futures && !futures.isEmpty()) {
            for (Future<Boolean> future : futures) {
                if (future.isDone() && !future.isCancelled()) {
                    try {
                        results.add(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        logger.warn("动态脚本执行获取结果失败", e);
                    }
                }
                else{
                    logger.warn("动态脚本执行任务被cancel");
                }
            }
        }
    }

    private boolean executeGroovyField(AbstractFraudContext context, Map<String,WrappedGroovyObject> groovyObjectMap) {
        int failedCnt = 0;
        for (String fieldName : groovyObjectMap.keySet()) {

            String methodName = KunpengStringUtils.replaceJavaVarNameNotSupportChar(fieldName);
            Object value;
            try {
                long t1 = System.currentTimeMillis();
                GroovyObject groovyObject = groovyObjectMap.get(fieldName).getGroovyObject();
                value = executeGroovy(context, groovyObject, methodName);
                long t2 = System.currentTimeMillis();
                if(t2 - t1 > 30){
                    logger.warn("动态脚本执行时间过长, fieldName : {}, methodName : {}", fieldName, methodName);
                }
                context.setFieldValue(fieldName, value);
            } catch(Throwable ex) {
                failedCnt++;
            }
        }
        if (failedCnt > 0) {
            return false;
        } else {
            return true;
        }
    }

    private Object executeGroovy(AbstractFraudContext context, GroovyObject groovyObject, String methodName) {
        Object[] args = new Object[] {context};
        Object value = groovyObject.invokeMethod(methodName, args);
        return value;
    }

    class GroovyRunTask implements Callable<Boolean> {
        private AbstractFraudContext context;
        private Map<String,WrappedGroovyObject> groovyObjectMap;

        public GroovyRunTask(AbstractFraudContext context, Map<String,WrappedGroovyObject> groovyObjectMap) {
            this.context = context;
            this.groovyObjectMap = groovyObjectMap;
        }

        @Override
        public Boolean call() throws Exception {
            return executeGroovyField(context, groovyObjectMap);
        }
    }
}
