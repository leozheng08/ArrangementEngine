package cn.tongdun.kunpeng.api.engine.cache;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 需要批量远程调用的数据缓存
 * @author: zhongxiang.wang
 * @date: 2021-01-28 17:06
 */
public class BatchRemoteCallDataCache extends AbstractLocalCache<String, Map<String,List<Object>>> {

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
}
