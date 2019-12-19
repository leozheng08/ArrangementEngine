package cn.tongdun.kunpeng.api.runmode;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 策略执行模式缓存 policyUuid -> AbstractRunMode
 * @Author: liang.chen
 * @Date: 2019/12/17 上午11:47
 */
@Component
public class RunModeCache {
    //policyUuid -> AbstractRunMode
    private Map<String,AbstractRunMode> runModeMap = new ConcurrentHashMap<>();


    public AbstractRunMode getRunMode(String uuid){
        return runModeMap.get(uuid);
    }

    public void putRunMode(String uuid,AbstractRunMode runMode){
        runModeMap.put(uuid,runMode);
    }

    public void removeRunMode(String uuid){
        runModeMap.remove(uuid);
    }
}
