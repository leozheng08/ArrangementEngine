package cn.tongdun.kunpeng.api.infrastructure.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import cn.tongdun.kunpeng.share.config.IConfigRepository;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2020/2/25 下午3:34
 */
public class DynamicLoadPropertySource extends MapPropertySource implements IConfigRepository{

    private Logger logger = LoggerFactory.getLogger(DynamicLoadPropertySource.class);
    private long lastModified = 0;
    private String fileName;

    //对于值有多个通过,号分隔，例：key=a,b,c ，将文本转换为List,Set缓存。
    private Map<String,Set<String>> configDataOfSet = new HashMap<>();
    private Map<String,List<String>> configDataOfList = new HashMap<>();
    //如果值有发生变化时回调的接口
    private Map<String,Runnable> callbackMap = new HashMap<>();




    public DynamicLoadPropertySource(String name,String fileName) {
        super(name, new ConcurrentHashMap<String, Object>(64));
        this.fileName = fileName;
        refresh();
    }

    /**
     * 数据刷新，获取资源信息
     * @return
     */
    public void refresh() {
        Map<String, Object> map = dynamicLoadProperties();
        if(map.isEmpty()){
            return ;
        }

        Map oldConfig = new HashMap(this.source);
        Iterator<Map.Entry<String,Object>> it = this.source.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,Object> entry = it.next();
            if(!map.containsKey(entry.getKey())){
                it.remove();
            }
        }
        this.source.putAll(map);

        callback(oldConfig, this.source);
        configDataOfSet = new HashMap<>();
        configDataOfList = new HashMap<>();
        logger.info("config changed -> \n {}", JSON.toJSONString(this.source, true));
    }

    /**
     * 获取配置文件信息
     * @return
     */
    public Map<String, Object> dynamicLoadProperties() {
        Properties props = new Properties();
        Map<String, Object> map = new HashMap<String, Object>();
        InputStream in = null;
        try {
            File file = new File(fileName);
            if(file.lastModified()<=lastModified){
                //文件未更新
                return map;
            }
            in = new FileInputStream(file);
            props.load(in);
            Enumeration<?> en = props.propertyNames();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                String property = props.getProperty(key);
                map.put(key, property);
            }
            lastModified = file.lastModified();
            logger.info("load app.properties:{}",map);
        } catch (Exception e) {
            logger.error("getProperties error",e);
        } finally {
            if(in != null){
                try{
                    in.close();
                }catch (Exception e){
                }
            }
        }
        return map;
    }








    @Override
    public boolean getBooleanProperty(String name,boolean defaultValue) {
        if(this.source == null || !this.source.containsKey(name)){
            return defaultValue;
        }
        return "1".equals(this.source.get(name)) || "true".equals(this.source.get(name));
    }


    @Override
    public int getIntProperty(String name, int defaultValue){
        if(this.source == null){
            return defaultValue;
        }
        return toInteger(this.source.get(name), defaultValue);
    }

    @Override
    public long getLongProperty(String name, long defaultValue){
        if(this.source == null){
            return defaultValue;
        }
        return toLong(this.source.get(name), defaultValue);
    }

    @Override
    public String getStringProperty(String name, String defaultValue){
        Object data = this.source.get(name);
        return data == null ? defaultValue : String.valueOf(data);
    }

    @Override
    public Set<String> getSetProperty(String name, String defaultValue) {
        if (configDataOfSet.get(name) != null) {
            return configDataOfSet.get(name);
        }
        Set setTmp = null;
        if (this.source == null || this.source.get(name) == null) {
            if (defaultValue == null) {
                setTmp = Collections.EMPTY_SET;
            } else {
                setTmp = Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().splitToList(defaultValue));
            }
        } else {
            Object data = this.source.get(name);
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
        if(this.source == null || this.source.get(name) == null){
            if (defaultValue == null) {
                listTmp = Collections.EMPTY_LIST;
            } else {
                listTmp = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(defaultValue);
            }
        } else {
            Object data = this.source.get(name);
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


}