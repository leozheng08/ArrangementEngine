package cn.tongdun.kunpeng.api.engine.model.application;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.engine.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 应用信息缓存 appName -> AdminApplication
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午8:01
 */
@Component
public class AdminApplicationCache extends AbstractLocalCache<String, AdminApplication> {

    private static final Logger logger = LoggerFactory.getLogger(AdminApplicationCache.class);

    //patnerCode.appName -> AdminApplication
    private LoadingCache<String, AdminApplication> adminApplicationTimingCache;

    //patnerCode -> appName
    private BiMap<String, Set<String>> partnerApplicationCache = HashBiMap.create();

    //patnerCode.appName -> secretkey
    private BiMap<String, String> appNameToSecretKey = HashBiMap.create();
    //secretkey -> patnerCode.appName
    private BiMap<String, String> secretKeyToAppName = appNameToSecretKey.inverse();

    @Autowired
    private IAdminApplicationRepository iAdminApplicationRepository;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 2, 30,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(10),
            new ThreadFactoryBuilder().setNameFormat("admin-application-thread-%d").build());

    @PostConstruct
    public void init() {
        register(AdminApplication.class);

        final CacheLoader<String, AdminApplication> loader = new CacheLoader<String, AdminApplication>() {
            @Override
            public AdminApplication load(String key) throws Exception {
                logger.info("AdminApplicationCache::load key: {}", key);
                return loadByKey(key);
            }

            //异步加载缓存
            @Override
            public ListenableFuture<AdminApplication> reload(String key, AdminApplication oldValue) throws Exception {
                //定义任务。
                ListenableFutureTask<AdminApplication> futureTask = ListenableFutureTask.create(() -> {
                    logger.info("AdminApplicationCache::reload key: {}, partnerCode: {}, appName: {}", key, oldValue == null ? "" : oldValue.getPartnerCode(), oldValue == null ? "" : oldValue.getAppName());
                    return loadByKey(key);
                });
                //异步执行任务
                threadPoolExecutor.execute(futureTask);
                return futureTask;
            }
        };

        RemovalListener<String, AdminApplication> removalListener = notification -> {
            AdminApplication value = notification.getValue();
            if (null == value) {
                appNameToSecretKey.remove(value.getAppName());
                secretKeyToAppName.remove(value.getSecretKey());
            }
        };

        adminApplicationTimingCache = CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.HOURS).removalListener(removalListener).build(loader);
    }

    private AdminApplication loadByKey(String key) {
        AdminApplication result = new AdminApplication();
        try {
            String[] values = StringUtils.split(key, ".");
            if (values != null && values.length == 2) {
                AdminApplicationDTO adminApplicationDTO = iAdminApplicationRepository.selectApplicationByPartnerAppName(values[0], values[1]);
                AdminApplication adminApplication = new AdminApplication();
                BeanUtils.copyProperties(adminApplicationDTO, adminApplication);
                result = adminApplication;
            } else {
                logger.error("load partnerCode and appName key invalidate :{}", key);
            }
        } catch (Exception e) {
            logger.error("load partnerCode and appName" + key + " :{}", e);
        }
        return result;
    }

    @Override
    public AdminApplication get(String key) {
        try {
            AdminApplication adminApplication = adminApplicationTimingCache.get(key);
            return adminApplication == null || StringUtils.isEmpty(adminApplication.getSecretKey()) ? null : adminApplication;
        } catch (ExecutionException e) {
            logger.error(TraceUtils.getFormatTrace() + "adminApplicationTimingCache.get error", e);
            return null;
        }
    }

    public AdminApplication get(String partnerCode, String appName) {
        try {
            AdminApplication adminApplication = adminApplicationTimingCache.get(generateKey(partnerCode, appName));
            return adminApplication == null || StringUtils.isEmpty(adminApplication.getSecretKey()) ? null : adminApplication;
        } catch (ExecutionException e) {
            logger.error(TraceUtils.getFormatTrace() + "adminApplicationTimingCache.get error", e);
            return null;
        }
    }

    @Override
    public void put(String key, AdminApplication adminApplication) {
        adminApplicationTimingCache.put(key, adminApplication);
        appNameToSecretKey.forcePut(key, adminApplication.getSecretKey());
        Set<String> appSet = partnerApplicationCache.get(adminApplication.getPartnerCode());
        if (appSet == null) {
            appSet = Sets.newHashSet();
            partnerApplicationCache.put(adminApplication.getPartnerCode(), appSet);
        }
        appSet.add(adminApplication.getAppName());
    }

    @Override
    public AdminApplication remove(String key) {
        try {
            AdminApplication adminApplication = adminApplicationTimingCache.get(key);
            appNameToSecretKey.remove(key);
            adminApplicationTimingCache.refresh(key);
            return adminApplication;
        } catch (ExecutionException e) {
            logger.error(TraceUtils.getFormatTrace() + "adminApplicationTimingCache.get error", e);
            return null;
        }
    }


    public void put(String partnerCode, String appName, AdminApplication adminApplication) {
        put(generateKey(partnerCode, appName), adminApplication);
    }

    public void remove(AdminApplication adminApplication) {
        String key = generateKey(adminApplication.getPartnerCode(), adminApplication.getAppName());
        remove(key);
    }


    public String generateKey(String partnerCode, String appName) {
        return StringUtils.join(partnerCode, ".", appName);

    }


    public AdminApplication getBySecretKey(String secretKey) {
        String partnerCodeAppName = secretKeyToAppName.get(secretKey);
        if (partnerCodeAppName == null) {
            return null;
        }
        return get(partnerCodeAppName);
    }

    public String getPartnerCodeAppNameBySecretKey(String secretKey) {
        String partnerCodeAppName = secretKeyToAppName.get(secretKey);
        return partnerCodeAppName;
    }


    public void addAdminApplication(AdminApplication adminApplication) {
        String key = generateKey(adminApplication.getPartnerCode(), adminApplication.getAppName());
        put(key, adminApplication);
    }

    public Set<String> getApplications(String partnerCode) {
        return partnerApplicationCache.get(partnerCode);
    }
}