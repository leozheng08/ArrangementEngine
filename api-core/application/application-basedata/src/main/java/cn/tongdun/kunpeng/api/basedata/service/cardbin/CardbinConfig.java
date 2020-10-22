package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import cn.tongdun.kunpeng.api.engine.model.application.AdminApplicationCache;
import cn.tongdun.kunpeng.api.engine.model.dictionary.Dictionary;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.api.engine.model.dictionary.IDictionaryRepository;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: changkai.yang
 * @Date: 2020/7/24 下午6:11
 */
@Component
public class CardbinConfig {

    private static final Logger logger = LoggerFactory.getLogger(CardbinConfig.class);

    @Autowired
    IDictionaryRepository dictionaryRepository;

    /**
     * 表空间1
     */
    public static final String NAMESPACE_1 = "1";
    /**
     * 表空间2
     */
    public static final String NAMESPACE_2 = "2";
    /**
     * 缓存的key
     */
    public static final String CACHE_KEY_PREFIX = "card_bin_cache";
    /**
     * cardbin数据存储配置的key，value为 1：表空间1, 2：表空间2
     */
    public static final String CONFIG_KEY = "card_bin_ns";
    public static final String SWITH_KEY = "card_bin_used_redis_cache";

    private String nameSpace = "card_bin_cache1";

    private boolean cardbinUsedReidsCache = false;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public boolean isCardbinUsedReidsCache() {
        return cardbinUsedReidsCache;
    }

    public void setCardbinUsedReidsCache(boolean cardbinUsedReidsCache) {
        this.cardbinUsedReidsCache = cardbinUsedReidsCache;
    }

    @PostConstruct
    public void init(){
        //初始化加载一次配置
        loadConfig();

        //定时刷新
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    loadConfig();
                } catch (Exception e) {
                    logger.error(TraceUtils.getFormatTrace()+"定时刷新Cardbin配置缓存异常",e);
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    private void loadConfig(){
        try {
            List<Dictionary> dictList = dictionaryRepository.getDictionary(CONFIG_KEY);
            if(CollectionUtils.isNotEmpty(dictList)){
                String ns = dictList.get(0).getValue();
                if(StringUtils.equalsAny(ns, "1", "2")){
                    this.nameSpace = CACHE_KEY_PREFIX + dictList.get(0).getValue();
                }
            }

            List<Dictionary> dictList2 = dictionaryRepository.getDictionary(SWITH_KEY);
            if(CollectionUtils.isNotEmpty(dictList2)){
                String value = dictList2.get(0).getValue();
                if(StringUtils.equalsAnyIgnoreCase(value, "true")){
                    this.cardbinUsedReidsCache = true;
                }else {
                    this.cardbinUsedReidsCache = false;
                }
            }
        } catch (Exception e) {
            logger.error("查询Cardbin表空间配置信息出错", e);
        }
    }
}
