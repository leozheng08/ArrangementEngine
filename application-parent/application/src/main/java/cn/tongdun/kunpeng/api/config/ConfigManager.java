package cn.tongdun.kunpeng.api.config;

import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.share.config.AbstractConfigRepository;
import cn.tongdun.kunpeng.share.config.IConfigRepository;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @Author: liang.chen
 * @Date: 2019/11/29 下午4:25
 */
@Component("configManager")
public class ConfigManager implements IConfigRepository{

    @Autowired
    private Environment env;

    public String getProperty(String name){
        return env.getProperty(name);
    }


    private Logger logger = LoggerFactory.getLogger(AbstractConfigRepository.class);

    //对于值有多个通过,号分隔，例：key=a,b,c ，将文本转换为List,Set缓存。
    private Map<String,Set<String>> configDataOfSet = new HashMap<>();
    private Map<String,List<String>> configDataOfList = new HashMap<>();

    //eventType->businussType
    private Map<String,String> eventType2BusinussMap = new HashMap<>();
    /**
     * 根据配置的key取得boolean值
     * @param configKey
     * @return
     */
    @Override
    public boolean isConfig(String configKey) {
        return isConfig(configKey,false);
    }

    /**
     * 根据配置的key取得boolean值,如果未配置，则返回默认值
     * @param configKey
     * @param defaultValue
     * @return
     */
    @Override
    public boolean isConfig(String configKey,boolean defaultValue) {
        String value = getProperty(configKey);
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return "1".equals(value) || "true".equals(value);
    }


    @Override
    public boolean isNotConfig(String configKey){
        return !isConfig(configKey);
    }


    /**
     * 根据配置的key取得Integer值
     * @param configKey
     * @param defaultValue
     * @return
     */
    @Override
    public int getIntegerConfigData(String configKey, int defaultValue){
        String value = getProperty(configKey);
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return toInteger(value, defaultValue);
    }

    /**
     * 根据配置的key取得Long值
     * @param configKey
     * @param defaultValue
     * @return
     */
    @Override
    public long getLongConfigData(String configKey, long defaultValue){
        String value = getProperty(configKey);
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return toLong(value, defaultValue);
    }

    /**
     * 根据配置的key取得String值
     * @param configKey
     * @return
     */
    @Override
    public String getConfigData(String configKey){
        return getConfigData(configKey, "");
    }

    /**
     * 根据配置的key取得String值,如果没有配置，则返回默认值
     * @param configKey
     * @param defaultValue
     * @return
     */
    @Override
    public String getConfigData(String configKey, String defaultValue){
        String value = getProperty(configKey);
        if(StringUtils.isBlank(value)){
            return defaultValue;
        }
        return value;
    }

    /**
     * 根据配置的key取得Set<String> ，如果配置的值为string,根据,号分隔
     * @param configKey
     * @return
     */
    @Override
    public  Set<String> getConfigDataOfSet(String configKey){
        return getConfigDataOfSet(configKey, null);
    }

    /**
     * 根据配置的key取得Set<String> ，如果配置的值为string,根据,号分隔,如果未配置，则返回默认值
     * @param configKey
     * @param defaultValue
     * @return
     */
    @Override
    public Set<String> getConfigDataOfSet(String configKey, String defaultValue) {
        if (configDataOfSet.get(configKey) != null) {
            return configDataOfSet.get(configKey);
        }
        Set setTmp = null;
        String value = getProperty(configKey);
        if(StringUtils.isBlank(value)){
            if (defaultValue == null) {
                setTmp = Collections.EMPTY_SET;
            } else {
                setTmp = Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(defaultValue));
            }
        } else {
            setTmp = Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(String.valueOf(value)));
        }
        configDataOfSet.put(configKey,setTmp);
        return setTmp;
    }

    /**
     * 根据配置的key取得List<String> ，如果配置的值为string,根据,号分隔
     * @param configKey
     * @return
     */
    @Override
    public List<String> getConfigDataOfList(String configKey){
        return getConfigDataOfList(configKey, null);
    }

    /**
     * 根据配置的key取得List<String> ，如果配置的值为string,根据,号分隔,如果未配置，则返回默认值
     * @param configKey
     * @param defaultValue
     * @return
     */
    @Override
    public List<String> getConfigDataOfList(String configKey, String defaultValue){
        if (configDataOfList.get(configKey) != null) {
            return configDataOfList.get(configKey);
        }

        List listTmp = null;
        String value = getProperty(configKey);
        if(StringUtils.isBlank(value)){
            if (defaultValue == null) {
                listTmp = Collections.EMPTY_LIST;
            } else {
                listTmp = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(defaultValue);
            }
        } else {
            listTmp = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(String.valueOf(value));
        }
        configDataOfList.put(configKey,listTmp);
        return listTmp;
    }


    /**
     * 如果修改了某个属性名，则回调方法，可以是属性名前缀, 例：abc*,则为key为abc前缀的有变更时则回调。
     * @param key
     * @param callback
     */
    @Override
    public void addCallback(String key,Runnable callback ){
        throw new RuntimeException("not support!");
    }

    /**
     * 取消回调方法
     * @param key
     */
    @Override
    public void removeCallback(String key){
        throw new RuntimeException("not support!");
    }

    private static Integer toInteger(Object obj, Integer defVal) {
        if(obj == null){
            return defVal;
        }
        if(obj instanceof Integer){
            return (Integer)obj;
        }
        try {
            return Integer.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }

    private static Long toLong(Object obj, Long defVal) {
        if(obj == null){
            return defVal;
        }
        try {
            return Long.valueOf(obj.toString());
        } catch (Exception e) {
            return defVal;
        }
    }


    public String getBusinessByEventType(String eventType){
        return getBusinessByEventType(eventType, Constant.BUSINESS_ANTI_FRAUD);
    }

    public String getBusinessByEventType(String eventType,String defaultValue){
        if(eventType2BusinussMap.isEmpty()){
            String value = getProperty("business.event.type");
            if(StringUtils.isBlank(value)){
                return defaultValue;
            }

            try{
                JSONObject json = JSONObject.parseObject(value);
                for(Map.Entry<String, Object> entry:json.entrySet()){
                    JSONArray array = (JSONArray)entry.getValue();
                    if(array == null){
                        continue;
                    }
                    for(Object obj:array){
                        eventType2BusinussMap.put(obj.toString(),entry.getKey());
                    }
                }
            } catch (Exception e){
                logger.error("getBusinessByEventType error",e);
            }
        }

        String businuss = eventType2BusinussMap.get(eventType);
        if(StringUtils.isBlank(businuss)){
            return defaultValue;
        }
        return businuss;

    }

}
