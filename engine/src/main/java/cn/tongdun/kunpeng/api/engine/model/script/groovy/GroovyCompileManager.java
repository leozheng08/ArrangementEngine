package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
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
    private GroovyFieldCache groovyFieldCache;


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

        String key = groovyFieldCache.bulidKey(partnerCode , appName , eventType);
        GroovyField groovyField = groovyFieldCache.get(key);

        // 为null则缓存中没有，重新编译新的类和方法
        if (null == groovyField) {
            groovyField = new GroovyField();
            GroovyClassGenerator generator = new GroovyClassGenerator(key);
            generator.init();
            String methodName = replaceJavaVarNameNotSupportChar(field);
            generator.appendMethod(methodName, methodBody);

            GroovyObject groovyObject = generator.compileGroovySource();
            groovyField.setGroovyObject(groovyObject);
            groovyField.getFieldMethods().put(field, methodBody);
            groovyFieldCache.put(key, groovyField);
            return;
        }

        // 否则直接追加方法，然后重新编译
        groovyField.getFieldMethods().put(field, methodBody);
        reCompileSource(key, groovyField);
        return;
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
    public void removeMethod(String partnerCode, String appName, String eventType, String field)
            throws CompilationFailedException,
            InstantiationException,
            IllegalAccessException,
            IOException {
        String key =  groovyFieldCache.bulidKey(partnerCode , appName , eventType);
        GroovyField groovyField = groovyFieldCache.get(key);

        if (null == groovyField) {
            return;
        }

        groovyField.getFieldMethods().remove(field);
        reCompileSource(key, groovyField);
    }



    // 重新编译源码
    private void reCompileSource(String key, GroovyField groovyField) throws CompilationFailedException,
            InstantiationException,
            IllegalAccessException, IOException {
        if (null == key || null == groovyField) {
            return;
        }

        if (groovyField.getFieldMethods().isEmpty()) {
            remove(key);
            return;
        }

        Map<String, String> fieldMethods = groovyField.getFieldMethods();
        GroovyClassGenerator generator = new GroovyClassGenerator(key);
        generator.init();
        groovyField = new GroovyField();

        for (Map.Entry<String, String> fm : fieldMethods.entrySet()) {
            groovyField.getFieldMethods().put(fm.getKey(), fm.getValue());
            generator.appendMethod(replaceJavaVarNameNotSupportChar(fm.getKey()), fm.getValue());
        }

        GroovyObject groovyObject = generator.compileGroovySource();

        groovyField.setGroovyObject(groovyObject);
        groovyFieldCache.put(key, groovyField);
    }


    public void remove(String partnerCode, String appName, String eventType) {
        String key = groovyFieldCache.bulidKey(partnerCode , appName , eventType);
        groovyFieldCache.remove(key);
    }


    private void remove(String key) {
        groovyFieldCache.remove(key);
    }


    /**
     * 替换掉java变量名不支持的字符(允许的是: 下划线或者字母开头,后边允许加数字)<br>
     * 并加 m_ 开头
     *
     * @param fieldName
     * @return
     */
    public static String replaceJavaVarNameNotSupportChar(String fieldName) {
        return ("m_" + fieldName).replaceAll("[^a-zA-Z0-9_]", "");
    }


    public void warmAllGroovyFields() {
        Set<String> keys = groovyFieldCache.keySet();
        for (String key : keys) {
            GroovyField field = groovyFieldCache.get(key);
            warmGroovyField(field);
        }
    }

    private void warmGroovyField( GroovyField field) {
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
        context.setAccountMobile("13712341234");
        context.set("accountEmail", "hello@world.com");
        context.setEventId("login");
        context.setEventType("Login");
        context.setEventOccurTime(new Date());
        return context;
    }



    private boolean executeGroovyField(AbstractFraudContext context, GroovyField field) {
        int failedCnt = 0;
        for (String fieldName : field.getFieldMethods().keySet()) {


            String methodName = replaceJavaVarNameNotSupportChar(fieldName);
            Object value;


            try {
                long t1 = System.currentTimeMillis();
                value = executeGroovyField(field.getGroovyObject(), methodName, context);
                long t2 = System.currentTimeMillis();
                if(t2 - t1 > 30){
                    logger.warn("动态脚本执行时间过长,fieldName:{},methodName:{}", fieldName, methodName);
                }

                context.setFieldValue(fieldName, value);
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
