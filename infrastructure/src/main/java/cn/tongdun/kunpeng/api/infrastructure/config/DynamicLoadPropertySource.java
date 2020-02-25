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
 * 动态从本地文件加载配置，依赖定时器刷新
 * @Author: liang.chen
 * @Date: 2020/2/25 下午3:34
 */
public class DynamicLoadPropertySource extends MapPropertySource {

    private Logger logger = LoggerFactory.getLogger(DynamicLoadPropertySource.class);
    private long lastModified = 0;
    private String fileName;

    public DynamicLoadPropertySource(String name,String fileName) {
        super(name, new ConcurrentHashMap<String, Object>(64));
        this.fileName = fileName;
        refresh();
    }

    /**
     * 数据刷新，获取资源信息.
     * 由定时器调用此方法完成刷新
     * @return
     */
    public boolean refresh() {
        Map<String, Object> map = dynamicLoadProperties();
        if(map.isEmpty()){
            return false;
        }

        //新配置没有包含的配置去除
        Iterator<Map.Entry<String,Object>> it = this.source.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,Object> entry = it.next();
            if(!map.containsKey(entry.getKey())){
                it.remove();
            }
        }

        //设置新的配置数据
        this.source.putAll(map);

        //刷新成功
        return true;
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
                //文件未更新,则不刷新
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
            logger.info("load {fileName}:{}",fileName,map);
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

}