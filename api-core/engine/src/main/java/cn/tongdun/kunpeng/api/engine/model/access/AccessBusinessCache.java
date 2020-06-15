package cn.tongdun.kunpeng.api.engine.model.access;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplicationCache;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author: yuanhang
 * @date: 2020-06-10 14:28
 **/
@Component
public class AccessBusinessCache extends AbstractLocalCache<String, AccessBusiness> {

    @Autowired
    AdminApplicationCache adminApplicationCache;

    /**
     * appName -> accessBusiness
     */
    public Map<String, AccessBusiness> accessBusinessMap = Maps.newConcurrentMap();

    @PostConstruct
    public void init() {
        register(AccessBusiness.class);
    }

    @Override
    public AccessBusiness get(String key) {
        return accessBusinessMap.get(key);
    }

    @Override
    public void put(String uuid, AccessBusiness accessBusiness) {
        accessBusinessMap.put(uuid, accessBusiness);
    }

    @Override
    public AccessBusiness remove(String appName) {
        return accessBusinessMap.remove(appName);
    }

    public Map<String, AccessBusiness> getAccessBusinessMap() {
        return accessBusinessMap;
    }
}
