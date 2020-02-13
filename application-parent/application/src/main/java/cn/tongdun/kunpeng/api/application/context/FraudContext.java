package cn.tongdun.kunpeng.api.application.context;

import cn.fraudmetrix.horde.biz.entity.IpReputationRulesObj;
import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:24
 */
@Data
public class FraudContext extends AbstractFraudContext{


    private GeoipEntity geoipEntity;                        //function AddressMatch
    private IpReputationRulesObj ipReputationRulesObj;      //function Proxy





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
    public Object getPolicyIndex(String indexId) {
        return null;
    }

    @Override
    public void saveDetail(String ruleUuid, String conditionUuid, DetailCallable detailCallable) {

    }
}
