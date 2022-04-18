package cn.fraudmetrix.module.riskbase.service.intf;

import cn.fraudmetrix.module.riskbase.object.IdInfo;

public interface IdInfoQueryService extends RiskBaseQueryService {

    /**
     * 根据身份证号查找行政区划信息
     *
     * @param idNumber
     * @return
     */
    public IdInfo getIdInfo(String idNumber);
}
