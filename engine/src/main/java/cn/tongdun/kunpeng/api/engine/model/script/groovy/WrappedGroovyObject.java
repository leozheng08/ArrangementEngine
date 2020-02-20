package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import groovy.lang.GroovyObject;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WrappedGroovyObject implements Serializable{

    private static final long serialVersionUID = 2785807641911113679L;
    private GroovyObject        groovyObject;                                // 编译后的对象
    private String              source;                                      // 源代码
    private Map<String, String> fieldMethods = new ConcurrentHashMap<>();    // 字段和方法体

    public GroovyObject getGroovyObject() {
        return groovyObject;
    }

    public void setGroovyObject(GroovyObject groovyObject) {
        this.groovyObject = groovyObject;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, String> getFieldMethods() {
        return fieldMethods;
    }

    public void setFieldMethods(Map<String, String> fieldMethods) {
        this.fieldMethods = fieldMethods;
    }

}
