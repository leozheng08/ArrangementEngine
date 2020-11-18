package cn.tongdun.kunpeng.api.engine.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangchangkai
 * @date 2020/11/16
 */
public class EventMsgRegister {
    private static Logger logger = LoggerFactory.getLogger(EventMsgRegister.class);

    @Autowired
    private EventMsgParser eventMsgParser;

    private Map<String, String> entityMap = new HashMap<>();

    public Map<String, String> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<String, String> entityMap) {
        this.entityMap = entityMap;
    }

    public void init() throws ClassNotFoundException{
        for(String key:entityMap.keySet()){
            String className = entityMap.get(key);
            Class entityClass = null;
            entityClass = Class.forName(className);
            eventMsgParser.registerMsgEntity(key, entityClass);
        }
    }
}
