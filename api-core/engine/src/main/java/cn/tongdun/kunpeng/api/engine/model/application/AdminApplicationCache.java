package cn.tongdun.kunpeng.api.engine.model.application;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.StringUtils;
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
    //patnerCode.appName -> AdminApplication
    private Map<String,AdminApplication> adminApplicationMap = new ConcurrentHashMap<>();

    //patnerCode.appName -> secretkey
    private BiMap<String,String> appNameToSecretKey = HashBiMap.create();
    //secretkey -> patnerCode.appName
    private BiMap<String,String> secretKeyToAppName =  appNameToSecretKey.inverse();

    @PostConstruct
    public void init(){
        register(AdminApplication.class);
    }

    @Override
    public AdminApplication get(String key){
        return adminApplicationMap.get(key);
    }

    public AdminApplication get(String partnerCode, String appName){
        return adminApplicationMap.get(generateKey(partnerCode,appName));
    }

    @Override
    public void put(String key, AdminApplication adminApplication){
        adminApplicationMap.put(key,adminApplication);
        appNameToSecretKey.forcePut(key,adminApplication.getSecretKey());
    }


    public void put(String partnerCode, String appName, AdminApplication adminApplication){
        put(generateKey(partnerCode,appName),adminApplication);
    }



    @Override
    public AdminApplication remove(String key){
        return adminApplicationMap.remove(key);
    }

    public AdminApplication remove(String partnerCode, String appName){
        return remove(generateKey(partnerCode,appName));
    }



    public String generateKey(String partnerCode, String appName) {
        return StringUtils.join(partnerCode,".",appName);

    }


    public AdminApplication getBySecretKey(String secretKey){
        String appName = secretKeyToAppName.get(secretKey);
        if(appName == null){
            return null;
        }
        return adminApplicationMap.get(appName);
    }


    public void addAdminApplication(AdminApplication adminApplication){
        String key = generateKey(adminApplication.getPartnerCode(),adminApplication.getCode());
        put(key,adminApplication);
    }
}
