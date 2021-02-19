package cn.tongdun.kunpeng.api.engine.cache;

import cn.hutool.core.map.MapUtil;
import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description: 需要批量远程调用的数据缓存
 * @author: zhongxiang.wang
 * @date: 2021-01-28 17:06
 */
@Component
public class BatchRemoteCallDataCache implements ILocalCache<String, Map<String,List<Object>>> {

    /**
     * 数据结构
     * <policyuuid,<template,List<AbstractBatchRemoteCallData>>>
     * @see AbstractBatchRemoteCallData
     */
    private ConcurrentHashMap<String,Map<String,List<Object>>> cache = new ConcurrentHashMap<>(20);

    @Override
    public Map<String, List<Object>> get(String key) {
        return cache.get(key);
    }

    @Override
    public void put(String key, Map<String, List<Object>> value) {
        cache.put(key,value);
    }

    @Override
    public Map<String, List<Object>> remove(String uuid) {
        return cache.remove(uuid);
    }

    /**
     * 添加或更新缓存
     * @param policyUuid 策略uuid
     * @param template 模版类型
     * @param ruleUuid 规则uuid
     * @param batchRemoteCallDatas 批量调用数据
     */
    public void addOrUpdate(String policyUuid,String template,String ruleUuid,List<Object> batchRemoteCallDatas){
        Map<String, List<Object>> datas = cache.get(policyUuid);
        //此policyUuid下没有数据
        if(CollectionUtils.isEmpty(datas)){
            cache.put(policyUuid, MapUtil.of(template,batchRemoteCallDatas));
        } else {
            List<Object> objects = datas.get(template);
            //此template下没有数据
            if (CollectionUtils.isEmpty(objects)) {
                datas.put(template,batchRemoteCallDatas);
            } else {
                for (int i = 0;i < objects.size(); i ++){
                    AbstractBatchRemoteCallData callData = (AbstractBatchRemoteCallData)objects.get(i);
                    //缓存中有此ruleUuid的数据 删除后重新添加
                    if(ruleUuid.equals(callData.getRuleUuid())){
                        //由于AbstractBatchRemoteCallData的实现类属性不同，无法重写通用的equals/hashcode方法，这里无法使用remove(Object)方法
                        objects.remove(i);
                        //弥补删除时数据前移一位带来的索引位移
                        i --;
                    }
                }
                objects.addAll(batchRemoteCallDatas);
                datas.put(template,objects);
            }
            cache.put(policyUuid,datas);
        }
    }

    /**
     * 删除缓存
     * 删除改策略下，该模版类型下，该规则的批量远程调用数据缓存
     * @param policyUuid 策略uuid
     * @param template 模版类型
     * @param ruleUuid 规则uuid
     */
    public void remove(String policyUuid,String template,String ruleUuid){
        Map<String, List<Object>> batchDatas = cache.get(policyUuid);
        if(!CollectionUtils.isEmpty(batchDatas)){
            List<Object> datas = batchDatas.get(template);
            if(!CollectionUtils.isEmpty(datas)){
                //由于AbstractBatchRemoteCallData的实现类属性不同，这里无法使用remove方法，因为属性不确定，无法重写通用equals/hashcode，所以直接覆盖写，而不是remove
                List<Object> result = datas.stream().filter(obj -> !ruleUuid.equals(((AbstractBatchRemoteCallData) obj).getRuleUuid())).collect(Collectors.toList());
                batchDatas.put(template,result);
                cache.put(policyUuid,batchDatas);
            }
        }
    }
}
