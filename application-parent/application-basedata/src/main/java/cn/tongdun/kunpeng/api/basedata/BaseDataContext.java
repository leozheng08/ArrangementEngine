package cn.tongdun.kunpeng.api.basedata;

import cn.fraudmetrix.horde.biz.entity.IpReputationRulesObj;
import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import lombok.Data;


@Data
public class BaseDataContext extends FraudContext {
    private GeoipEntity geoipEntity;                        //function AddressMatch
    private IpReputationRulesObj ipReputationRulesObj;      //function Proxy
}
