package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.tongdun.kunpeng.api.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScriptField;
import groovy.lang.GroovyObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
     * 根据partnerCode eventType 更新缓存中该对象的信息，<br>
     * 如果缓存中不存在，则说明没有编译过，编译新的并放到缓存中
     *
     * @param script
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws CompilationFailedException
     */
    public void addOrUpdate(DynamicScript script)
            throws CompilationFailedException,
            InstantiationException,
            IllegalAccessException,
            IOException {

        //国内反欺诈
        if (CollectionUtils.isNotEmpty(script.getScriptFieldList())) {
            for (DynamicScriptField scriptField : script.getScriptFieldList()) {
                String field = scriptField.getFieldCode();
                String methodBody = script.getScriptCode();
                if (StringUtils.isAnyBlank(field, methodBody)) {
                    return;
                }

                WrappedGroovyObject groovyField = new WrappedGroovyObject();
                String className = "groovy_" + script.getUuid();
                GroovyClassGenerator generator = new GroovyClassGenerator(className);
                generator.init();
                String methodName = KunpengStringUtils.replaceJavaVarNameNotSupportChar(field);
                generator.appendMethod(methodName, methodBody);

                GroovyObject groovyObject = generator.compileGroovySource();
                groovyField.setGroovyObject(groovyObject);
                groovyField.getFieldMethods().put(field, methodBody);
                groovyField.setSource(generator.getSource().toString());
                groovyField.setUuid(script.getUuid());
                groovyField.setGmtModify(script.getGmtModify());
                groovyField.setPartnerCode(scriptField.getPartnerCode());
                groovyField.setAppName(scriptField.getAppName());
                groovyField.setEventType(scriptField.getEventType());
                groovyField.setAssignField(field);
                groovyField.setFieldMethodName(methodName);

                groovyFieldCache.put(script.getUuid(), groovyField);
            }
        } else {//老逻辑不动
            String field = script.getAssignField();
            String methodBody = script.getScriptCode();

            if (StringUtils.isAnyBlank(field, methodBody)) {
                return;
            }

            WrappedGroovyObject groovyField = new WrappedGroovyObject();
            String className = "groovy_" + script.getUuid();
            GroovyClassGenerator generator = new GroovyClassGenerator(className);
            generator.init();
            String methodName = KunpengStringUtils.replaceJavaVarNameNotSupportChar(field);
            generator.appendMethod(methodName, methodBody);

            GroovyObject groovyObject = generator.compileGroovySource();
            groovyField.setGroovyObject(groovyObject);
            groovyField.getFieldMethods().put(field, methodBody);
            groovyField.setSource(generator.getSource().toString());
            groovyField.setUuid(script.getUuid());
            groovyField.setGmtModify(script.getGmtModify());
            groovyField.setPartnerCode(script.getPartnerCode());
            groovyField.setAppName(script.getAppName());
            groovyField.setEventType(script.getEventType());
            groovyField.setAssignField(script.getAssignField());
            groovyField.setFieldMethodName(methodName);

            groovyFieldCache.put(script.getUuid(), groovyField);
        }

    }

    public void remove(String uuid) {
        groovyFieldCache.remove(uuid);
    }


}
