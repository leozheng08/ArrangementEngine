package cn.tongdun.kunpeng.api.engine.model.application;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.cache.*;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 应用信息缓存 appName -> AdminApplication
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class AdminApplicationCache extends AbstractLocalCache<String,AdminApplication> {

    private static final Logger logger = LoggerFactory.getLogger(AdminApplicationCache.class);

    //patnerCode.appName -> AdminApplication
    private LoadingCache<String, AdminApplication> adminApplicationTimingCache;

    //patnerCode.appName -> secretkey
    private BiMap<String,String> appNameToSecretKey = HashBiMap.create();
    //secretkey -> patnerCode.appName
    private BiMap<String,String> secretKeyToAppName =  appNameToSecretKey.inverse();

    @Autowired
    private IAdminApplicationRepository iAdminApplicationRepository;

    @PostConstruct
    public void init(){
        register(AdminApplication.class);

        final CacheLoader<String,AdminApplication> loader = new CacheLoader<String, AdminApplication>() {
            @Override
            public AdminApplication load(String key) throws Exception {
                return loadByKey(key);
            }
        };

        RemovalListener<String,AdminApplication> removalListener = notification -> {
            AdminApplication value = notification.getValue();
            if(null != value){
                appNameToSecretKey.remove(value.getAppName());
                secretKeyToAppName.remove(value.getSecretKey());
            }
        };

        adminApplicationTimingCache = CacheBuilder.newBuilder().refreshAfterWrite(4, TimeUnit.HOURS).removalListener(removalListener).build(loader);
    }

    private AdminApplication loadByKey(String key) {
        AdminApplication result =  new AdminApplication();
        String[] values = StringUtils.split(key, ".");
        if(values != null && values.length == 2){
            AdminApplicationDTO adminApplicationDTO = iAdminApplicationRepository.selectApplicationByPartnerAppName(values[0], values[1]);
            AdminApplication adminApplication = new AdminApplication();
            BeanUtils.copyProperties(adminApplicationDTO,adminApplication);
        }
        return result;
    }

    @Override
    public AdminApplication get(String key){
        try {
            AdminApplication adminApplication = adminApplicationTimingCache.get(key);
            return adminApplication == null || StringUtils.isEmpty(adminApplication.getSecretKey()) ? null : adminApplication;
        } catch (ExecutionException e) {
            logger.error(TraceUtils.getFormatTrace() + "adminApplicationTimingCache.get error", e);
            return null;
        }
    }

    public AdminApplication get(String partnerCode, String appName){
        try {
            AdminApplication adminApplication = adminApplicationTimingCache.get(generateKey(partnerCode,appName));
            return adminApplication == null || StringUtils.isEmpty(adminApplication.getSecretKey()) ? null : adminApplication;
        } catch (ExecutionException e) {
            logger.error(TraceUtils.getFormatTrace() + "adminApplicationTimingCache.get error", e);
            return null;
        }
    }

    @Override
    public void put(String key, AdminApplication adminApplication){
        adminApplicationTimingCache.put(key,adminApplication);
        appNameToSecretKey.forcePut(key,adminApplication.getSecretKey());
    }

    @Override
    public AdminApplication remove(String uuid) {
        return null;
    }


    public void put(String partnerCode, String appName, AdminApplication adminApplication){
        put(generateKey(partnerCode,appName),adminApplication);
    }




    public String generateKey(String partnerCode, String appName) {
        return StringUtils.join(partnerCode,".",appName);

    }


    public AdminApplication getBySecretKey(String secretKey){
        String partnerCodeAppName = secretKeyToAppName.get(secretKey);
        if(partnerCodeAppName == null){
            return null;
        }
        return get(partnerCodeAppName);
    }

    public String getPartnerCodeAppNameBySecretKey(String secretKey) {
        String partnerCodeAppName = secretKeyToAppName.get(secretKey);
        return partnerCodeAppName;
    }


    public void addAdminApplication(AdminApplication adminApplication){
        String key = generateKey(adminApplication.getPartnerCode(),adminApplication.getAppName());
        put(key,adminApplication);
    }
}
