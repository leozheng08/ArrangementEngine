package cn.tongdun.kunpeng.api.engine.model.script;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.GroovyObjectCache;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.WrappedGroovyObject;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import cn.tongdun.tdframework.core.util.TaskWrapLoader;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 子策略执行，根据subPolicyUuid从缓存中取得子策略实体SubPolicy对象后运行。
 *
 * @Author: huangjin
 */
@Component
public class DefaultDynamicScriptManager implements DynamicScriptManager {
    private final static Logger logger = LoggerFactory.getLogger(DefaultDynamicScriptManager.class);

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

    @Override
    public boolean execute(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        try {
            handleField(context);
        } catch (Exception e) {
            // 暂不处理动态脚本执行超时的状态码，以日志为准
            if (!ReasonCodeUtil.isTimeout(e)) {
                logger.error(TraceUtils.getFormatTrace() + "动态脚本调用异常", e);
                ReasonCodeUtil.add(context, ReasonCode.GROOVY_EXECUTE_ERROR, "groovy");
            }
        }
        return true;
    }


//    private void handleField(AbstractFraudContext context) {
//        List<WrappedGroovyObject> groovyObjectList = new ArrayList<>(50);
//        String classKey = null;
//        // 合作方指定事件类型
//        classKey = context.getPartnerCode() + context.getEventType();
//        List<WrappedGroovyObject> tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
//        if (null != tmpGroovyObjects) {
//            groovyObjectList.addAll(tmpGroovyObjects);
//        }
//        // 合作方全部事件类型
//        classKey = context.getPartnerCode() + "all";
//        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
//        if (null != tmpGroovyObjects) {
//            groovyObjectList.addAll(tmpGroovyObjects);
//        }
//        // 全局指定事件类型
//        classKey =  "all" + context.getEventType();
//        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
//        if (null != tmpGroovyObjects) {
//            groovyObjectList.addAll(tmpGroovyObjects);
//        }
//        // 全局全部事件类型
//        classKey = "all" + "all" ;
//        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
//        if (null != tmpGroovyObjects) {
//            groovyObjectList.addAll(tmpGroovyObjects);
//        }
//        if (groovyObjectList.isEmpty()) {
//            return;
//        }
//
//        List<Callable<Boolean>> tasks =new ArrayList<>(groovyObjectList.size());
//        for (WrappedGroovyObject wrappedGroovyObject : groovyObjectList) {
//            tasks.add(TaskWrapLoader.getTaskWrapper().wrap(new GroovyRunTask(context, wrappedGroovyObject)));
//        }
//        List<Future<Boolean>> futures = null;
//        long t1 = System.currentTimeMillis();
//        try {
//            futures = executeThreadPool.invokeAll(tasks,500,TimeUnit.MILLISECONDS);
//        } catch (InterruptedException | NullPointerException e) {
//            logger.warn(TraceUtils.getFormatTrace()+"动态脚本执行出错", e);
//        } catch (RejectedExecutionException e) {
//            logger.error(TraceUtils.getFormatTrace()+"动态脚本丢弃执行", e);
//        }
//        long t2 = System.currentTimeMillis();
//
//        if(t2 - t1 > 30){
//            logger.warn(TraceUtils.getFormatTrace()+"动态脚本执行时间太长, groovy_execute_too_long costTime : {}", t2-t1);
//        }
//
//        List<Boolean> results = new ArrayList(futures!= null?futures.size():0);
//        if (null != futures && !futures.isEmpty()) {
//            for (Future<Boolean> future : futures) {
//                if (future.isDone() && !future.isCancelled()) {
//                    try {
//                        results.add(future.get());
//                    } catch (InterruptedException | ExecutionException e) {
//                        logger.warn(TraceUtils.getFormatTrace()+"动态脚本执行获取结果失败", e);
//                    }
//                }
//                else{
//                    logger.warn(TraceUtils.getFormatTrace()+"动态脚本执行任务被cancel");
//                }
//            }
//        }
//    }

    private void handleField(AbstractFraudContext context) {
        List<WrappedGroovyObject> groovyObjectList = new ArrayList<>(50);
        String classKey = null;
        /**
         * 合作方指定应用指定事件类型
         */
        classKey = context.getPartnerCode() + "-" + context.getAppName() + "-" + context.getEventType();
        List<WrappedGroovyObject> tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
        if (null != tmpGroovyObjects) {
            groovyObjectList.addAll(tmpGroovyObjects);
        }
        /**
         * 合作方指定应用全部事件类型
         */
        classKey = context.getPartnerCode() + "-" + context.getAppName() + "-all";
        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
        if (null != tmpGroovyObjects) {
            groovyObjectList.addAll(tmpGroovyObjects);
        }

        /**
         * 合作方全部应用指定事件类型
         */
        classKey = context.getPartnerCode() + "-" + "all" + "-" + context.getEventType();
        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
        if (null != tmpGroovyObjects) {
            groovyObjectList.addAll(tmpGroovyObjects);
        }
        /**
         * 合作方全部应用全部事件类型
         */
        classKey = context.getPartnerCode() + "-all" + "-all";
        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
        if (null != tmpGroovyObjects) {
            groovyObjectList.addAll(tmpGroovyObjects);
        }
        /**
         * 全部合作方全部应用指定事件类型
         */
        classKey = "all-all-" + context.getEventType();
        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
        if (null != tmpGroovyObjects) {
            groovyObjectList.addAll(tmpGroovyObjects);
        }
        /**
         *全部合作方全部应用全部事件类型
         */
        classKey = "all" + "-all" + "-all";
        tmpGroovyObjects = groovyObjectCache.getByScope(classKey);
        if (null != tmpGroovyObjects) {
            groovyObjectList.addAll(tmpGroovyObjects);
        }
        if (groovyObjectList.isEmpty()) {
            return;
        }

        List<Callable<Boolean>> tasks = new ArrayList<>(groovyObjectList.size());
        for (WrappedGroovyObject wrappedGroovyObject : groovyObjectList) {
            tasks.add(TaskWrapLoader.getTaskWrapper().wrap(new GroovyRunTask(context, wrappedGroovyObject)));
        }
        List<Future<Boolean>> futures = null;
        long t1 = System.currentTimeMillis();
        try {
            futures = executeThreadPool.invokeAll(tasks, 500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | NullPointerException e) {
            logger.warn(TraceUtils.getFormatTrace() + "动态脚本执行出错", e);
        } catch (RejectedExecutionException e) {
            logger.error(TraceUtils.getFormatTrace() + "动态脚本丢弃执行", e);
        }
        long t2 = System.currentTimeMillis();

        if (t2 - t1 > 30) {
            logger.warn(TraceUtils.getFormatTrace() + "动态脚本执行时间太长, groovy_execute_too_long costTime : {}", t2 - t1);
        }

        List<Boolean> results = new ArrayList(futures != null ? futures.size() : 0);
        if (null != futures && !futures.isEmpty()) {
            for (Future<Boolean> future : futures) {
                if (future.isDone() && !future.isCancelled()) {
                    try {
                        results.add(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        logger.warn(TraceUtils.getFormatTrace() + "动态脚本执行获取结果失败", e);
                    }
                } else {
                    logger.warn(TraceUtils.getFormatTrace() + "动态脚本执行任务被cancel");
                }
            }
        }
    }

    private boolean executeGroovyField(AbstractFraudContext context, WrappedGroovyObject wrappedGroovyObject) {
        String fieldName = wrappedGroovyObject.getAssignField();
        String methodName = wrappedGroovyObject.getFieldMethodName();
        Object value;
        try {
            long t1 = System.currentTimeMillis();
            GroovyObject groovyObject = wrappedGroovyObject.getGroovyObject();
            value = executeGroovy(context, groovyObject, methodName);
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 30) {
                logger.warn(TraceUtils.getFormatTrace() + "动态脚本执行时间过长, fieldName : {}, methodName : {}", fieldName, methodName);
            }
            context.setField(fieldName, value);
        } catch (Throwable ex) {
//            logger.error(TraceUtils.getFormatTrace() + "动态脚本执行失败, fieldName :{}, methodName :{}", fieldName, methodName);
            throw ex;
        }

        return true;
    }

    private Object executeGroovy(AbstractFraudContext context, GroovyObject groovyObject, String methodName) {
        Object[] args = new Object[]{context};
        Object value = groovyObject.invokeMethod(methodName, args);
        return value;
    }

    class GroovyRunTask implements Callable<Boolean> {
        private AbstractFraudContext context;
        private WrappedGroovyObject wrappedGroovyObject;

        public GroovyRunTask(AbstractFraudContext context, WrappedGroovyObject wrappedGroovyObject) {
            this.context = context;
            this.wrappedGroovyObject = wrappedGroovyObject;
        }

        @Override
        public Boolean call() throws Exception {
            return executeGroovyField(context, wrappedGroovyObject);
        }
    }
}
