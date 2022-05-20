package cn.fraudmetrix.module.riskbase.service.intf;


import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;

public interface MobileInfoQueryService extends RiskBaseQueryService {

    public MobileInfoDO getMobileInfo(String phoneNumber);
}
