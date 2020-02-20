package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author: liang.chen
 * @Date: 2020/2/19 下午10:04
 */
@Component
public class GroovyObjectCache {

    //scope(适用范围) -> fieldName(字段名) -> WrappedGroovyObject(包含编译后groovy对象)
    //scope(适用范围)包含：
    //   合作方指定事件类型: context.getPartnerCode() + context.getAppName() + context.getEventType()
    //   合作方全部事件类型: context.getPartnerCode() + context.getAppName() + "All";
    //   全局指定事件类型:   "All" + "All" + context.getEventType();
    //   全局全部事件类型:   "All" + "All" + "All";
    private Map<String, Map<String,WrappedGroovyObject>> groovyFields = new ConcurrentHashMap<String, Map<String,WrappedGroovyObject>>(30); // 缓存编译后的对象


    public Map<String,WrappedGroovyObject> get(String key) {
        return groovyFields.get(key);
    }

    public WrappedGroovyObject get(String key, String fieldName) {
        Map<String,WrappedGroovyObject> map = groovyFields.get(key);
        if(map == null){
            return null;
        }
        return map.get(fieldName);
    }

    public void put(String key, String fieldName, WrappedGroovyObject groovyField) {
        Map<String,WrappedGroovyObject> map = groovyFields.get(key);
        if(map == null){
            map = new ConcurrentHashMap<>(30);
            Map<String,WrappedGroovyObject> oldMap = groovyFields.putIfAbsent(key,map);
            if (oldMap != null) {
                map = oldMap;
            }
        }
        map.put(fieldName,groovyField);
    }

    public WrappedGroovyObject remove(String key, String fieldName) {
        Map<String,WrappedGroovyObject> map = groovyFields.get(key);
        if(map == null){
            return null;
        }
        return map.remove(fieldName);
    }


    public Map<String,WrappedGroovyObject> remove(String key) {
        return groovyFields.remove(key);
    }

    public Set<String> keySet() {
        return groovyFields.keySet();
    }


    public String generateKey(String partnerCode, String appName, String eventType) {
            return StringUtils.join(partnerCode,appName,eventType);

    }
}
