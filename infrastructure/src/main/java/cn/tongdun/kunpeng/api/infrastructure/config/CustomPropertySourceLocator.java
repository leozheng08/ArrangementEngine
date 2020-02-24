package cn.tongdun.kunpeng.api.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/24 下午7:49
 */
@Configuration
public class CustomPropertySourceLocator  {
    private final static Logger logger = LoggerFactory.getLogger(CustomPropertySourceLocator.class);
    @Bean
    public MapPropertySource systemEnv() {
        //这里读取到所有的环境变量并放入这个map即可
        Map<String,String> envMap = System.getenv();
        Map<String,Object> envObjectMap = new HashMap<>(envMap);
        for(Map.Entry<String,String> entry:envMap.entrySet()){
            logger.info("[env]{}={}",entry.getKey(),entry.getValue());
        }
        return new MapPropertySource("customProperty",
                envObjectMap);
    }

}