package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;


/**
 * @Author: huangjin
 */
public interface GroovyObjectCache {


    void put(String uuid, WrappedGroovyObject wrappedGroovyObject);

    WrappedGroovyObject remove(String uuid);

    WrappedGroovyObject get(String uuid);

    List<WrappedGroovyObject> getByScope(String key);

    Collection<WrappedGroovyObject> getAll();

    String generateKey(String partnerCode, String eventType);


    void putList(String policyUuid, List<String> scriptUuids);
}
