package cn.tongdun.kunpeng.api.application.context;

import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:24
 */
@Data
public class FraudContext extends AbstractFraudContext {


    @Override
    public Object getField(String name) {
        return get(name);
    }

    @Override
    public void setField(String name, Object value) {
        this.setFieldValue(name, value);
    }

    /**
     * 规则执行过程中的用于保存由function产生的详情，不含指标平台产生的详情。
     * 目前分线程执行只在子策略的维度，在没有通用规则之前都是线程安全的，因为一个规则只会属于一个子策略。
     *
     * @param ruleUuid
     * @param conditionUuid
     * @param detailCallable
     */
    @Override
    public void saveDetail(String ruleUuid, String conditionUuid, DetailCallable detailCallable) {
        Map<String, DetailCallable> conditionMap = functionHitDetail.get(ruleUuid);
        if (null == conditionMap) {
            conditionMap = new ConcurrentHashMap<>(2);
            functionHitDetail.put(ruleUuid, conditionMap);
        }
        conditionMap.put(conditionUuid, detailCallable);
    }
}
