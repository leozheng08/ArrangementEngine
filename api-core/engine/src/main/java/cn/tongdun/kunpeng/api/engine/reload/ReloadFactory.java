package cn.tongdun.kunpeng.api.engine.reload;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/9 下午12:29
 */
@Component
public class ReloadFactory {

    private Map<Class, IReload> reloadMap = new HashMap<>(16);

    public IReload getReload(Class clazz) {
        return reloadMap.get(clazz);
    }

    public void register(Class clazz,IReload reload){
        reloadMap.put(clazz, reload);
    }
}
