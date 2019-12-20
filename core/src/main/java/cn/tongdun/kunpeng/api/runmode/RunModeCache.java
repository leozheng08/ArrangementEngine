package cn.tongdun.kunpeng.api.runmode;

import cn.tongdun.kunpeng.api.cache.AbstractLocalCache;
import cn.tongdun.kunpeng.api.cache.ILocalCache;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略执行模式缓存 policyUuid -> AbstractRunMode
 * @Author: liang.chen
 * @Date: 2019/12/17 上午11:47
 */
@Component
public class RunModeCache extends AbstractLocalCache<String,AbstractRunMode>{
    //policyUuid -> AbstractRunMode
    private Map<String,AbstractRunMode> runModeMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init(){
        register(AbstractRunMode.class);
    }

    @Override
    public AbstractRunMode get(String uuid){
        return runModeMap.get(uuid);
    }

    @Override
    public void put(String uuid, AbstractRunMode runMode){
        runModeMap.put(uuid,runMode);
    }

    @Override
    public void remove(String uuid){
        runModeMap.remove(uuid);
    }
}
