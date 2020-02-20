package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroovyFieldCache extends AbstractLocalCache<String,GroovyField> {

    private Map<String, GroovyField> GROOVY_FIELDS = new ConcurrentHashMap<String, GroovyField>(30); // 缓存编译后的对象


    @PostConstruct
    public void init(){
        register(DynamicScript.class);
    }

    @Override
    public GroovyField get(String key) {
        return GROOVY_FIELDS.get(key);
    }



    @Override
    public void put(String key, GroovyField groovyField) {
        GROOVY_FIELDS.put(key, groovyField);
    }

    @Override
    public GroovyField remove(String key) {
        return GROOVY_FIELDS.remove(key);
    }

    public Set<String> keySet() {
        return GROOVY_FIELDS.keySet();
    }


    public String bulidKey(String partnerCode, String appName, String eventType) {
            return StringUtils.join(partnerCode,appName,eventType);

    }
}
