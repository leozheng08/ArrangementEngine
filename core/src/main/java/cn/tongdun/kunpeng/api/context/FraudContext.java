package cn.tongdun.kunpeng.api.context;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:24
 */
@Data
public class FraudContext extends AbstractFraudContext implements ExecuteContext {

    boolean eventTypeIsCredit;

    @Override
    public Object getField(String var1){
        return get(var1);
    }

    @Override
    public Double getIndex(String var1){
        return null;
    }

    @Override
    public Double getOriginIndex(String var1){
        return null;
    }




}
