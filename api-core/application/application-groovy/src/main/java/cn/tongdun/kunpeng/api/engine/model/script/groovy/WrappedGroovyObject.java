package cn.tongdun.kunpeng.api.engine.model.script.groovy;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import groovy.lang.GroovyObject;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class WrappedGroovyObject extends VersionedEntity {

    private static final long serialVersionUID = 2785807641911113679L;
    /**
     * 合作方 partner_code
     */
    private String partnerCode;

    /**
     * 应用名称 app_name
     */
    private String appName;

    /**
     * 事件类型 event_type
     */
    private String eventType;


    private GroovyObject groovyObject;                                // 编译后的对象
    private String source;                                      // 源代码
    private String fieldMethodName;                             // 方法名
    private String assignField;                                 // 赋值的字段名

    //国内反欺诈拆分
    private List<String> keys;                                  //scopeKey
    private List<String> fieldCodes;                            //分配的字段

    private Map<String, String> fieldMethods = new ConcurrentHashMap<>();    // 字段和方法体

}
