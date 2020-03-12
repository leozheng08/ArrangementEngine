package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author: liang.chen
 * @Date: 2020/2/19 下午10:04
 */
@Component
public class GroovyObjectCache {

    //dynamicScriptUuid -> WrappedGroovyObject
    private Map<String,WrappedGroovyObject> groovyMap = new ConcurrentHashMap<>(30); // 缓存编译后的对象

    //scope(适用范围) -> WrappedGroovyObject(包含编译后groovy对象)
    private Map<String, Set<String>> scopeToGroovyMap = new ConcurrentHashMap<>(30); // 缓存编译后的对象

    //scope(适用范围) -> fieldName(字段名) -> WrappedGroovyObject(包含编译后groovy对象)
    //scope(适用范围)包含：
    //   合作方指定事件类型: context.getPartnerCode() + context.getEventType()
    //   合作方全部事件类型: context.getPartnerCode() + "All";
    //   全局指定事件类型:   "All" + context.getEventType();
    //   全局全部事件类型:   "All" + "All";
//    private Map<String, Map<String,WrappedGroovyObject>> groovyFields = new ConcurrentHashMap<String, Map<String,WrappedGroovyObject>>(30); // 缓存编译后的对象


    public void put(String uuid, WrappedGroovyObject wrappedGroovyObject){
        groovyMap.put(uuid,wrappedGroovyObject);

        String key = generateKey(wrappedGroovyObject.getPartnerCode() , wrappedGroovyObject.getEventType());
        Set dynamicScriptUuidSet = scopeToGroovyMap.get(key);
        if(dynamicScriptUuidSet == null){
            dynamicScriptUuidSet = new ConcurrentHashSet();
            Set oldDynamicScriptUuidSet = scopeToGroovyMap.putIfAbsent(key,dynamicScriptUuidSet);
            if(oldDynamicScriptUuidSet != null){
                dynamicScriptUuidSet = oldDynamicScriptUuidSet;
            }
        }
        dynamicScriptUuidSet.add(uuid);
    }

    public WrappedGroovyObject remove(String uuid){
        WrappedGroovyObject wrappedGroovyObject = groovyMap.remove(uuid);

        if(wrappedGroovyObject == null){
            return null;
        }

        String key = generateKey(wrappedGroovyObject.getPartnerCode() , wrappedGroovyObject.getEventType());
        Set dynamicScriptUuidSet = scopeToGroovyMap.get(key);
        dynamicScriptUuidSet.remove(uuid);

        return wrappedGroovyObject;
    }

    public WrappedGroovyObject get(String uuid) {
        return groovyMap.get(uuid);
    }

    public List<WrappedGroovyObject> getByScope(String key) {
        Set<String> dynamicScriptUuidSet = scopeToGroovyMap.get(key);
        if(dynamicScriptUuidSet == null){
            return null;
        }
        List<WrappedGroovyObject> wrappedGroovyObjectList = new ArrayList<>();
        for(String dynamicScriptUuid:dynamicScriptUuidSet){
            WrappedGroovyObject wrappedGroovyObject = groovyMap.get(dynamicScriptUuid);
            if(wrappedGroovyObject != null){
                wrappedGroovyObjectList.add(wrappedGroovyObject);
            }
        }
        return wrappedGroovyObjectList;
    }

//    public WrappedGroovyObject get(String key, String fieldName) {
//        Map<String,WrappedGroovyObject> map = groovyFields.get(key);
//        if(map == null){
//            return null;
//        }
//        return map.get(fieldName);
//    }

//    public void put(String key, String fieldName, WrappedGroovyObject groovyField) {
//        Map<String,WrappedGroovyObject> map = groovyFields.get(key);
//        if(map == null){
//            map = new ConcurrentHashMap<>(30);
//            Map<String,WrappedGroovyObject> oldMap = groovyFields.putIfAbsent(key,map);
//            if (oldMap != null) {
//                map = oldMap;
//            }
//        }
//        map.put(fieldName,groovyField);
//    }

//    public WrappedGroovyObject remove(String key, String fieldName) {
//        Map<String,WrappedGroovyObject> map = groovyFields.get(key);
//        if(map == null){
//            return null;
//        }
//        return map.remove(fieldName);
//    }


//    public Map<String,WrappedGroovyObject> remove(String key) {
//        return groovyFields.remove(key);
//    }
//
//    public Set<String> keySet() {
//        return groovyFields.keySet();
//    }

    public Collection<WrappedGroovyObject> getAll(){
        return groovyMap.values();
    }


    public String generateKey(String partnerCode, String eventType) {
            return StringUtils.join(partnerCode,eventType);

    }
}
