package cn.tongdun.kunpeng.api.engine.cache;

import cn.hutool.core.map.MapUtil;
import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.load.step.PolicyLoadTask;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.RuleEventDO;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.SubPolicyEventDO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description: 需要批量远程调用的数据缓存
 * 使用场景：
 * 调整前
 * 比如关键词策略，如果一个子策略中，配置了多个关键词规则，之前的做法是，每一个关键词规则执行时都会去dubbo调用一次nlas，拿一个匹配结果回来
 * 调整后
 * 添加了一层BatchRemoteCallDataCache，在
 * @see cn.tongdun.kunpeng.api.engine.reload.impl.RuleReLoadManager#addOrUpdate(RuleEventDO)
 * @see cn.tongdun.kunpeng.api.engine.reload.impl.RuleReLoadManager#batchAddOrUpdate(List)
 * @see cn.tongdun.kunpeng.api.engine.reload.impl.SubPolicyReLoadManager#addOrUpdate(SubPolicyEventDO)
 * @see PolicyLoadTask#call()
 * 中，初始化及reload（启动时的全量load，event时的增量load）时，会把多个关键词规则的条件数据汇总起来，存入BatchRemoteCallDataCache中
 * 在KeywordStep中，从BatchRemoteCallDataCache拿出数据调用nlas的批量dubbo接口，去获取匹配结果存入AbstractFraudContext上下文中
 * 在keywordFunction中，拿到这个数据进行最终的处理，返回最终结果
 * 通过批量调用，减少n次dubbo调用，提升性能
 * 
 * 删除入口在
 * @see cn.tongdun.kunpeng.api.engine.reload.impl.RuleReLoadManager#remove(RuleEventDO) 
 * @see cn.tongdun.kunpeng.api.engine.reload.impl.SubPolicyReLoadManager#removeSubPolicy(String) 
 *
 * @see cn.tongdun.kunpeng.api.engine.reload.impl.PolicyReLoadManager 中不涉及add/update,涉及delete，因为add/update会下推到SubPolicyReLoadManager和RuleReLoadManager中执行，但是delete时，可以一把删除掉
 *
 *
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
        if (CollectionUtils.isEmpty(batchRemoteCallDatas)) {
            return;
        }
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
                        //暴力remove还有一个好处：自定义规则嵌套了多个支持批量的规则模版条件，其中一个规则模版条件进行了更新，这么直接移除覆盖，可以不用去比对这多个条件里究竟哪一个更新了
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
     * 删除该策略下，该模版类型下，该规则的批量远程调用数据缓存
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
                if (CollectionUtils.isEmpty(result)) {
                    //筛选后此模版下没有数据了
                    batchDatas.remove(template);
                } else {
                    batchDatas.put(template,result);
                }
                cache.put(policyUuid,batchDatas);
            }
            if (CollectionUtils.isEmpty(cache.get(policyUuid))) {
                cache.remove(policyUuid);
            }
        }
    }
}
