package cn.tongdun.kunpeng.api.engine.model.field;

import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.api.engine.cache.ILocalCache;

import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:45
 */
public interface FieldDefinitionCache<K, V> extends ILocalCache<K, V> {

    IFieldDefinition get(String uuid);

    void put(String uuid, IFieldDefinition fieldDefinition);

    IFieldDefinition remove(String uuid);

    Map<String, IFieldDefinition> getSystemField(String eventType);

    Map<String, IFieldDefinition> getExtendField(String partnerCode, String eventType);

    Map getSystemFieldMap();

    Map getExtendFieldMap();

}