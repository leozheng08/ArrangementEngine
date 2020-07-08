package cn.tongdun.kunpeng.api.engine.model.dictionary;

import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.cache.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryEnum.*;

/**
 * @Author: liuq
 * @Date: 2020/5/25 8:40 下午
 */
@Component
public class DictionaryManager {

    private Logger logger = LoggerFactory.getLogger(DictionaryManager.class);

    @Autowired
    private IDictionaryRepository dictionaryRepository;

    //Dict层面的缓存
    private LoadingCache<String, List<Dictionary>> dict10MinuteCache;
    //应用层面的缓存
    private ConcurrentMap<String, Object> appCache;
    private final Object appCacheLock = new Object();

    @PostConstruct
    public void init() {
        appCache = new ConcurrentHashMap<>(5);
        final CacheLoader<String, List<Dictionary>> loader = new CacheLoader<String, List<Dictionary>>() {
            @Override
            public List<Dictionary> load(String key) throws Exception {
                return loadDictionary(key);
            }
        };
        RemovalListener<String, List<Dictionary>> removalListener = new RemovalListener<String, List<Dictionary>>() {
            @Override
            public void onRemoval(RemovalNotification<String, List<Dictionary>> removal) {
                appCache.remove(removal.getKey());
                logger.info("监听移除DictionaryManager cacheKey:" + removal.getKey());
            }
        };
        dict10MinuteCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES).removalListener(removalListener).build(loader);

    }

    public List<Dictionary> loadDictionary(String key) {
        if (StringUtils.isBlank(key)) {
            return Collections.emptyList();
        }

        return dictionaryRepository.getDictionary(key);
    }

    /**
     * 获取FP调用的结果
     *
     * @return
     */
    public Map<String, String> getFpResultMap() {
        Object ret = appCache.get(StarkDeviceResult.name());
        if (null != ret) {
            return (Map<String, String>) ret;
        }
        synchronized (appCacheLock) {
            ret = appCache.get(StarkDeviceResult.name());
            if (null != ret) {
                return (Map<String, String>) ret;
            }
            List<Dictionary> dictionaryList = null;
            try {
                dictionaryList = dict10MinuteCache.get(StarkDeviceResult.name());
            } catch (Exception e) {
                logger.error(TraceUtils.getFormatTrace() + "getFpResultMap error", e);
            }

            if (null == dictionaryList || dictionaryList.isEmpty()) {
                appCache.put(StarkDeviceResult.name(), Collections.emptyMap());
                return Collections.emptyMap();
            }

            List<Map> listMap = JSON.parseArray(dictionaryList.get(0).getValue(), Map.class);
            Map<String, String> resultMap = Maps.newHashMap();
            for (Map map : listMap) {
                resultMap.put(map.get("code").toString(), map.get("desc").toString());
            }
            appCache.put(StarkDeviceResult.name(), resultMap);
            return resultMap;
        }
    }

    /**
     * 获取租户邮箱对应的key命名
     * @return
     */
    public List<Dictionary> getMailKey() {
        List<Dictionary> dictionaries = null;
        try {
            dictionaries = dict10MinuteCache.get(MailParamKey.name());
            if (null == dictionaries) {
                loadDictionary(MailParamKey.name());
                return dict10MinuteCache.get(MailParamKey.name());
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "get mail dictionary failed");
        }
        return dictionaries;
    }

    /**
     * 获取调用三方手机号接口配置
     * @return
     */
    public List<Dictionary> getPhoneSwitchKey() {
        List<Dictionary> dictionaries = null;
        try {
            dictionaries = dict10MinuteCache.get(PhoneSwitch.name());
            if (null == dictionaries) {
                loadDictionary(PhoneSwitch.name());
                return dict10MinuteCache.get(PhoneSwitch.name());
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "get phoneSwitch dictionary failed");
        }
        return dictionaries;
    }
}
