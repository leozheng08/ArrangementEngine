package cn.tongdun.kunpeng.api.engine.model.adminapplication;

import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.engine.model.subpolicy.SubPolicy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 子策略缓存subPolicyUuid -> SubPolicy
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class AdminApplicationCache extends AbstractLocalCache<String,AdminApplication> {

    //patnerCode.appName -> AdminApplication
    private Map<String,AdminApplication> adminApplicationMap = new ConcurrentHashMap<>();

    //secretkey -> AdminApplication
    private Map<String,AdminApplication> secretkeyMap = new ConcurrentHashMap<>();

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
    }


    public void put(String partnerCode, String appName, AdminApplication adminApplication){
        adminApplicationMap.put(generateKey(partnerCode,appName),adminApplication);
    }



    @Override
    public AdminApplication remove(String key){
        return adminApplicationMap.remove(key);
    }

    public AdminApplication remove(String partnerCode, String appName){
        return adminApplicationMap.remove(generateKey(partnerCode,appName));
    }



    public String generateKey(String partnerCode, String appName) {
        return StringUtils.join(partnerCode,".",appName);

    }


    public AdminApplication getBySecretKey(String secretKey){
        return secretkeyMap.get(secretKey);
    }

    public void putBySecretKey(String secretKey, AdminApplication adminApplication){
        secretkeyMap.put(secretKey,adminApplication);
    }

    public AdminApplication removeBySecretKey(String key){
        return secretkeyMap.remove(key);
    }


    public void addAdminApplication(AdminApplication adminApplication){
        put(adminApplication.getPartnerCode(),adminApplication.getName(),adminApplication);
        putBySecretKey(adminApplication.getSecretKey(),adminApplication);
    }


}
