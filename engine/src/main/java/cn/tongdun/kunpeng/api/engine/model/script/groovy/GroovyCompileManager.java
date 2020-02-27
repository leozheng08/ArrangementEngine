package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import groovy.lang.GroovyObject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2020/2/19 下午10:04
 */
@Component
public class GroovyCompileManager {

    private Logger logger = LoggerFactory.getLogger(GroovyCompileManager.class);


    @Autowired
    private GroovyObjectCache groovyFieldCache;


    /**
     * 根据partnerCode appName， eventType 更新缓存中该对象的信息，<br>
     * 如果缓存中不存在，则说明没有编译过，编译新的并放到缓存中
     *
     * @param partnerCode
     * @param appName
     * @param eventType
     * @param eventType
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws CompilationFailedException
     */
    public void addOrUpdate(String partnerCode, String appName, String eventType, String field, String methodBody)
            throws CompilationFailedException,
            InstantiationException,
            IllegalAccessException,
            IOException {
        if(StringUtils.isAnyBlank(field, methodBody)){
            return;
        }

        String key = groovyFieldCache.generateKey(partnerCode , appName , eventType);
        WrappedGroovyObject groovyField = new WrappedGroovyObject();
        GroovyClassGenerator generator = new GroovyClassGenerator(key);
        generator.init();
        String methodName = KunpengStringUtils.replaceJavaVarNameNotSupportChar(field);
        generator.appendMethod(methodName, methodBody);

        GroovyObject groovyObject = generator.compileGroovySource();
        groovyField.setGroovyObject(groovyObject);
        groovyField.getFieldMethods().put(field, methodBody);
        groovyField.setSource(generator.getSource().toString());
        groovyFieldCache.put(key, field, groovyField);
    }

    /**
     * 移除处理该字段的代码
     *
     * @param partnerCode
     * @param appName
     * @param eventType
     * @param field
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws CompilationFailedException
     */
    public void removeMethod(String partnerCode, String appName, String eventType, String field){
        String key =  groovyFieldCache.generateKey(partnerCode , appName , eventType);
        groovyFieldCache.remove(key,field);
    }



    public void remove(String partnerCode, String appName, String eventType) {
        String key = groovyFieldCache.generateKey(partnerCode , appName , eventType);
        groovyFieldCache.remove(key);
    }


    private void remove(String key) {
        groovyFieldCache.remove(key);
    }



    public void warmAllGroovyFields() {
        Set<String> keys = groovyFieldCache.keySet();
        for (String key : keys) {
            Map<String,WrappedGroovyObject> map = groovyFieldCache.get(key);
            for(WrappedGroovyObject groovyField:map.values()){
                warmGroovyField(groovyField);
            }
        }
    }

    private void warmGroovyField( WrappedGroovyObject field) {
        try {
            AbstractFraudContext context = mockFraudContext();
            executeGroovyField(context, field);
        } catch (Exception ex) {
            logger.error("动态脚本预热失败",ex);
        }
    }

    private static AbstractFraudContext mockFraudContext() {
        AbstractFraudContext context = new AbstractFraudContext(){
            @Override
            public Object getField(String var1){return null;}
            @Override
            public void setField(String var1, Object var2){}
            @Override
            public Double getPlatformIndex(String var1){return null;}
            @Override
            public Double getOriginPlatformIndex(String var1){return null;}
            @Override
            public Double getPolicyIndex(String var1){return null;}
            @Override
            public void saveDetail(String var1, String var2, DetailCallable var3){}
        };
        context.setPartnerCode("demo");
        context.setAppName("test");
        context.set("accountMobile","13712341234");
        context.set("accountEmail", "hello@world.com");
        context.setEventId("login");
        context.setEventType("Login");
        context.setEventOccurTime(new Date());
        return context;
    }



    private boolean executeGroovyField(AbstractFraudContext context, WrappedGroovyObject field) {
        int failedCnt = 0;
        for (String fieldName : field.getFieldMethods().keySet()) {


            String methodName = KunpengStringUtils.replaceJavaVarNameNotSupportChar(fieldName);
            Object value;


            try {
                long t1 = System.currentTimeMillis();
                value = executeGroovyField(field.getGroovyObject(), methodName, context);
                long t2 = System.currentTimeMillis();
                if(t2 - t1 > 30){
                    logger.warn("动态脚本执行时间过长,fieldName:{},methodName:{}", fieldName, methodName);
                }

                context.setField(fieldName, value);
            } catch(Throwable ex) {
                logger.error("动态脚本执行失败,fieldName:{},methodName:{}", fieldName, methodName,ex);
                failedCnt++;
            }
        }
        if (failedCnt > 0) {
            return false;
        } else {
            return true;
        }
    }

    private Object executeGroovyField(GroovyObject groovyObject, String methodName, AbstractFraudContext context) {
        Object[] args = new Object[] { context};
        Object value = groovyObject.invokeMethod(methodName, args);
        return value;
    }

}
