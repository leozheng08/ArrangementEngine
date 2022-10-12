package cn.tongdun.kunpeng.api.engine.model.dictionary;

import cn.tongdun.kunpeng.api.engine.dto.CommonDictDTO;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通用字典缓存：只解析通用格式的字典配置，字典为数组格式，有两个属性name和dName，这样缓存中的对象可以直接使用，而不需要再做一道解析转换。（原始字典缓存请使用 DictionaryManager）.
 * 字典配置示例：[{"dName":"设备类","name":"device"},{"dName":"自定义列表类","name":"customList"},{"dName":"位置类","name":"location"},{"dName":"时间类","name":"time"},{"dName":"频度类","name":"frequency"},{"dName":"IP画像类","name":"ipReputation"},{"dName":"手机画像类","name":"mobileReputation"},{"dName":"全局类","name":"global"},{"dName":"团伙类","name":"association"},{"dName":"其他","name":"other"},{"dName":"内容安全类","name":"contentSecurity"},{"dName":"关键词类","name":"keyword"}]
 * 每5分钟刷新一次缓存
 *
 * @Author: angle
 */
@Component
public class CommonDictionaryCache {

    private static final Logger logger = LoggerFactory.getLogger(CommonDictionaryCache.class);

    //key：mykey value：<name,dName>
    private LoadingCache<String, Map<String, String>> commonDictionaryCache;

    @Autowired
    private IDictionaryRepository dictionaryRepository;


    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 30,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(10),
            new ThreadFactoryBuilder().setNameFormat("common-dictionary-cache-%d").build());

    @PostConstruct
    public void init() {

        final CacheLoader<String, Map<String, String>> loader = new CacheLoader<String, Map<String, String>>() {
            @Override
            public Map<String, String> load(String key) throws Exception {
                logger.info("CommonDictionaryCache::load key: {}", key);
                return loadByKey(key);
            }

            //异步加载缓存
            @Override
            public ListenableFuture<Map<String, String>> reload(String key, Map<String, String> oldValue) throws Exception {
                //定义任务。
                ListenableFutureTask<Map<String, String>> futureTask = ListenableFutureTask.create(() -> {
                    logger.info("CommonDictionaryCache::reload key:{}, oldValue:{}", key, oldValue);
                    return loadByKey(key);
                });
                //异步执行任务
                threadPoolExecutor.execute(futureTask);
                return futureTask;
            }
        };

        //当前refreshAfterWrite过期刷新时机：并不是时间到了马上执行，而是等到有实际调用进来的时候，先返回旧的结果，再触发异步的刷新reload
        commonDictionaryCache = CacheBuilder.newBuilder().refreshAfterWrite(5, TimeUnit.MINUTES).build(loader);

        //第一次手动加载下
        try {
            commonDictionaryCache.get(DictionaryEnum.ruleBusinessTag.name());
        } catch (ExecutionException e) {
            logger.error("CommonDictionaryCache::init error", e);
        }
    }

    private Map<String, String> loadByKey(String key) {
        try {
            List<Dictionary> dictionaryList = dictionaryRepository.getDictionary(key);
            if (CollectionUtils.isEmpty(dictionaryList) || StringUtils.isBlank(dictionaryList.get(0).getValue())) {
                return Maps.newHashMap();
            }
            return JSON.parseArray(dictionaryList.get(0).getValue(), CommonDictDTO.class).stream().collect(Collectors.toMap(CommonDictDTO::getName, CommonDictDTO::getdName));
        } catch (Exception e) {
            logger.error("load common dictionary error, key:{} ", key, e);
            return Maps.newHashMap();
        }
    }


    /**
     * 获取字典值
     */
    public Map<String, String> getByKey(String key) {
        if (StringUtils.isBlank(key)) {
            return Maps.newHashMap();
        }
        try {
            return commonDictionaryCache.get(key);
        } catch (ExecutionException e) {
            logger.error("get {} common dictionary cache error.", key, e);
            return Maps.newHashMap();
        }
    }

    /**
     * 获取dName
     */
    public String getDName(String key, String name) {
        if (StringUtils.isAnyBlank(key, name)) {
            return null;
        }
        try {

            Map<String, String> value = commonDictionaryCache.get(key);
            if (value == null) {
                return null;
            }
            return value.get(name);

        } catch (ExecutionException e) {
            logger.error("get {}-{} common dictionary cache error.", key, name, e);
            return null;
        }
    }


}