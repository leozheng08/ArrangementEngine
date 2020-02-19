package cn.tongdun.kunpeng.api.application.context;

import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:24
 */
@Data
public class FraudContext extends AbstractFraudContext {


    @Override
    public Object getField(String name) {
        return null;
    }

    @Override
    public void setField(String name, Object value) {

    }

    @Override
    public Double getPlatformIndex(String indexId) {
        return null;
    }

    @Override
    public Double getOriginPlatformIndex(String indexId) {
        return null;
    }

    @Override
    public void saveDetail(String ruleUuid, String conditionUuid, DetailCallable detailCallable) {

    }
}
