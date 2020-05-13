package cn.tongdun.kunpeng.api.application.util;

import cn.tongdun.arch.dubbo.SeqIdContext;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.TaskWrapper;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @Author: liuq
 * @Date: 2020/5/13 10:13 上午
 */
public class KunpengTaskWrapper implements TaskWrapper {

    @Override
    public <T> Callable<T> wrap(Callable<T> t) {

        final Map mdcMap = MDC.getCopyOfContextMap();
        final ThreadContext threadContext = ThreadContext.getCopyOfContext();
        final String traceId= TraceUtils.getTrace();
        final String bindTag= SeqIdContext.get();
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                if (mdcMap != null) {
                    MDC.setContextMap(mdcMap);
                }
                ThreadContext.setContext(threadContext);
                TraceUtils.setTrace(traceId);
                if (null!=bindTag){
                    SeqIdContext.bind(bindTag);
                }
                try {
                    return t.call();
                } catch (Exception e) {
                    throw e;
                } finally {
                    TraceUtils.removeTrace();
                    if (null!=bindTag){
                        SeqIdContext.bind(null);
                    }
                    if (null != mdcMap) {
                        MDC.clear();
                    }
                    ThreadContext.clear();
                }
            }
        };
    }

    @Override
    public Runnable wrapRun(Runnable t) {

        final Map mdcMap = MDC.getCopyOfContextMap();
        final ThreadContext threadContext = ThreadContext.getCopyOfContext();
        final String traceId=TraceUtils.getTrace();
        final String bindTag= SeqIdContext.get();
        return new Runnable() {
            @Override
            public void run() {
                if (mdcMap != null) {
                    MDC.setContextMap(mdcMap);
                }
                if (threadContext != null) {
                    ThreadContext.setContext(threadContext);
                }
                TraceUtils.setTrace(traceId);
                if (null!=bindTag){
                    SeqIdContext.bind(bindTag);
                }
                try {
                    t.run();
                } catch (Exception e) {
                    throw e;
                } finally {
                    TraceUtils.removeTrace();
                    if (null!=bindTag){
                        SeqIdContext.bind(null);
                    }
                    if (null != mdcMap) {
                        MDC.clear();
                    }
                    ThreadContext.clear();
                }
            }
        };
    }
}
