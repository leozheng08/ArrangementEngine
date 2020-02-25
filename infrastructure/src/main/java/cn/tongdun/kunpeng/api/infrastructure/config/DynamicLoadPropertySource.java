package cn.tongdun.kunpeng.api.infrastructure.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;

/**
 * @Author: liang.chen
 * @Date: 2020/2/25 下午3:34
 */
public class DynamicLoadPropertySource extends MapPropertySource{

    private Logger logger = LoggerFactory.getLogger(DynamicLoadPropertySource.class);
    private long lastModified = 0;
    private String fileName;

    public DynamicLoadPropertySource(String name,String fileName) {
        super(name, new ConcurrentHashMap<String, Object>(64));
        this.fileName = fileName;
        refresh();
    }


    /**
     * //动态获取资源信息
     * @return
     */
    public void refresh() {
        Map<String, Object> map = dynamicLoadProperties();
        if(map.isEmpty()){
            return ;
        }
        Iterator<Map.Entry<String,Object>> it = this.source.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String,Object> entry = it.next();
            if(!map.containsKey(entry.getKey())){
                it.remove();
            }
        }
        this.source.putAll(map);
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

}