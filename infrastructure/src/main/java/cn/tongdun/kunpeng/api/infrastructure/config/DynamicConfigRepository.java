package cn.tongdun.kunpeng.api.infrastructure.config;

import cn.tongdun.kunpeng.share.config.IConfigRepository;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * 提供各方法以方便获取动态配置属性
 * @Author: liang.chen
 * @Date: 2020/2/25 下午10:47
 */
@Repository
public class DynamicConfigRepository implements IConfigRepository {

    private Logger logger = LoggerFactory.getLogger(DynamicConfigRepository.class);

    //对于值有多个通过,号分隔，例：key=a,b,c ，将文本转换为List,Set缓存。
    private Map<String,Set<String>> configDataOfSet = new HashMap<>();
    private Map<String,List<String>> configDataOfList = new HashMap<>();
    //如果值有发生变化时回调的接口
    private Map<String,Runnable> callbackMap = new HashMap<>();


    @Autowired
    private DynamicConfigInit dynamicConfigInit;


    @Override
    public Object getProperty(String name){
        if(dynamicConfigInit.getPropertySource().getSource() == null){
            return null;
        }
        return dynamicConfigInit.getPropertySource().getSource().get(name);
    }


    @Override
    public boolean getBooleanProperty(String name,boolean defaultValue) {
        if(dynamicConfigInit.getPropertySource().getSource() == null || !dynamicConfigInit.getPropertySource().getSource().containsKey(name)){
            return defaultValue;
        }
        return "1".equals(dynamicConfigInit.getPropertySource().getSource().get(name)) || "true".equals(dynamicConfigInit.getPropertySource().getSource().get(name));
    }


    @Override
    public int getIntProperty(String name, int defaultValue){
        if(dynamicConfigInit.getPropertySource().getSource() == null){
            return defaultValue;
        }
        return toInteger(dynamicConfigInit.getPropertySource().getSource().get(name), defaultValue);
    }

    @Override
    public long getLongProperty(String name, long defaultValue){
        if(dynamicConfigInit.getPropertySource().getSource() == null){
            return defaultValue;
        }
        return toLong(dynamicConfigInit.getPropertySource().getSource().get(name), defaultValue);
    }

    @Override
    public String getStringProperty(String name, String defaultValue){
        if(dynamicConfigInit.getPropertySource().getSource() == null){
            return defaultValue;
        }
        Object data = dynamicConfigInit.getPropertySource().getSource().get(name);
        return data == null ? defaultValue : String.valueOf(data);
    }

    @Override
    public Set<String> getSetProperty(String name, String defaultValue) {
        if (configDataOfSet.get(name) != null) {
            return configDataOfSet.get(name);
        }
        Set setTmp = null;
        if (dynamicConfigInit.getPropertySource().getSource() == null || dynamicConfigInit.getPropertySource().getSource().get(name) == null) {
            if (defaultValue == null) {
                setTmp = Collections.EMPTY_SET;
            } else {
                setTmp = Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(defaultValue));
            }
        } else {
            Object data = dynamicConfigInit.getPropertySource().getSource().get(name);
            setTmp = Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(String.valueOf(data)));
        }
        configDataOfSet.put(name,setTmp);
        return setTmp;
    }


    @Override
    public List<String> getListProperty(String name, String defaultValue){
        if (configDataOfList.get(name) != null) {
            return configDataOfList.get(name);
        }

        List listTmp = null;
        if(dynamicConfigInit.getPropertySource().getSource() == null || dynamicConfigInit.getPropertySource().getSource().get(name) == null){
            if (defaultValue == null) {
                listTmp = Collections.EMPTY_LIST;
            } else {
                listTmp = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(defaultValue);
            }
        } else {
            Object data = dynamicConfigInit.getPropertySource().getSource().get(name);
            listTmp = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(String.valueOf(data));
        }
        configDataOfList.put(name,listTmp);
        return listTmp;
    }


    //如果修改了某个属性名，则回调方法，可以是属性名前缀。
    @Override
    public void addCallback(String key,Runnable callback ){
        callbackMap.put(key,callback);
    }

    @Override
    public void removeCallback(String key){
        callbackMap.remove(key);
    }

    private void callback(String key,Set finishTask){
        if(StringUtils.isBlank(key)){
            return;
        }
        for(Map.Entry<String, Runnable> entry : callbackMap.entrySet()){
            if(key.startsWith(entry.getKey()) && !finishTask.contains(entry.getKey())){
                entry.getValue().run();
                finishTask.add(entry.getKey());
            }
        }
    }

    private void callback(Map<String, Object> oldMap,Map<String, Object> newMap){
        Set<String> leftKeySet = oldMap.keySet();
        Set<String> rightKeySet = newMap.keySet();
        //right的map有比对过的key值
        Set<String> finishedRight = new HashSet<>();

        //已回调过的任务
        Set<String> finishTask = new HashSet<>();

        for(String key : leftKeySet){
            Object left = oldMap.get(key);
            Object right = newMap.get(key);

            if(left != null && right != null){
                if(!left.equals(right)){
                    callback(key,finishTask);
                }
            } else {
                callback(key,finishTask);
            }

            finishedRight.add(key);
        }

        for(String key : rightKeySet){
            if(!finishedRight.contains(key)) {
                callback(key,finishTask);
            }
        }
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


    //定时刷新动态配置
    @Scheduled(cron = "*/10 * * * * ?")
    public void scheduling() {

        Map oldConfig = new HashMap();
        if(dynamicConfigInit.getPropertySource().getSource() == null){
            oldConfig.putAll(dynamicConfigInit.getPropertySource().getSource());
        }

        boolean success = dynamicConfigInit.getPropertySource().refresh();

        if(success) {
            callback(oldConfig, dynamicConfigInit.getPropertySource().getSource());
            configDataOfSet = new HashMap<>();
            configDataOfList = new HashMap<>();
            logger.info("config changed -> \n {}", JSON.toJSONString(dynamicConfigInit.getPropertySource().getSource(), true));
        }
    }
    

}
