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
    // subReasonCodeCache
    private ConcurrentMap<String, String> subReasonCodeCache;
    private final Object appCacheLock = new Object();
    private final Object subReasonCodeCacheLock = new Object();

    @PostConstruct
    public void init() {
        appCache = new ConcurrentHashMap<>(5);
        subReasonCodeCache = new ConcurrentHashMap<>(10);
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
                if (SubReasonCodeCacheData.name().equals(removal.getKey())) {
                    subReasonCodeCache.clear();
                }
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
     * 获取并解析组装三方子code配置
     *
     * @return e.g key:kunta100,value:20007,   key:message20007,value:"没有三方数据"
     */
    public Map<String, String> getSubReasonCodeMap() {
        if (null != subReasonCodeCache && subReasonCodeCache.size() > 0) {
            return subReasonCodeCache;
        }
        synchronized (subReasonCodeCacheLock) {
            if (null != subReasonCodeCache && subReasonCodeCache.size() > 0) {
                return subReasonCodeCache;
            }
            List<Dictionary> dictionaryList = null;
            try {
                dictionaryList = dict10MinuteCache.get(SubReasonCodeCacheData.name());
            } catch (Exception e) {
                logger.error(TraceUtils.getFormatTrace() + "getSubReasonCodeMap error", e);
            }

            if (null == dictionaryList || dictionaryList.isEmpty()) {
                subReasonCodeCache.put(SubReasonCodeCacheData.name(), "");
                return Collections.emptyMap();
            }

            Map<String,Object> serviceCodeMap = JSON.parseObject(dictionaryList.get(0).getValue(), Map.class);
            for (Map.Entry<String, Object> stringObjectEntry : serviceCodeMap.entrySet()) {
                if("message".equalsIgnoreCase(stringObjectEntry.getKey())) {
                    Map<String,String> messages = (Map) serviceCodeMap.get("message");
                    if(messages != null){
                        for (Map.Entry<String, String> message : messages.entrySet()) {
                            subReasonCodeCache.put("message" + message.getKey(), message.getValue());
                        }
                    }
                    continue;
                }

                Map<String,Object> codeConfigValue = (Map<String, Object>) stringObjectEntry.getValue();
                for (Map.Entry<String, Object> subCode : codeConfigValue.entrySet()) {
                    List<String> codes = (List<String>) subCode.getValue();

                    for (String code : codes) {
                        subReasonCodeCache.put(stringObjectEntry.getKey() + code, subCode.getKey());
                    }
                }
            }

            return subReasonCodeCache;
        }
    }

    public String getReasonCode(String subService, String subReasonCode){
        Map<String, String> reasons = getSubReasonCodeMap();
        String result = reasons.get(subService + subReasonCode);
        if(result != null){
            return result;
        }
        return "";
    }


    public String getMessage(String riskServiceCode){
        Map<String, String> reasons = getSubReasonCodeMap();
        String result = reasons.get("message" + riskServiceCode);
        if(result != null){
            return result;
        }
        return "";
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

    public List<Dictionary> getChangeToNewModelKey() {
        List<Dictionary> dictionaries = null;
        try {
            dictionaries = dict10MinuteCache.get("changeToNewModel");
            if (null == dictionaries) {
                loadDictionary("changeToNewModel");
                return dict10MinuteCache.get("changeToNewModel");
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "get changeToNewModel dictionary failed");
        }
        return dictionaries;
    }
}
