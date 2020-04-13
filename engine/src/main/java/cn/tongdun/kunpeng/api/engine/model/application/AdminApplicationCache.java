package cn.tongdun.kunpeng.api.engine.model.application;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用信息缓存 appName -> AdminApplication
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class AdminApplicationCache extends AbstractLocalCache<String,AdminApplication> {

    //appName -> AdminApplication
    private Map<String,AdminApplication> adminApplicationMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init(){
        register(AdminApplication.class);
    }

    @Override
    public AdminApplication get(String appName){
        return adminApplicationMap.get(appName);
    }

    @Override
    public void put(String appName, AdminApplication adminApplication){
        adminApplicationMap.put(appName,adminApplication);
    }

    @Override
    public AdminApplication remove(String appName){
        return adminApplicationMap.remove(appName);
    }
}
