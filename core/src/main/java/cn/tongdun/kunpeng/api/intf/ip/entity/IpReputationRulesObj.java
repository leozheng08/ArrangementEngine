package cn.tongdun.kunpeng.api.intf.ip.entity;


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * IpReputationRulesObj
 *  IP画像-规则对象
 * @author pandy(潘清剑)
 * @date 16/7/18
 */
public class IpReputationRulesObj implements Serializable{
    private long ipl;
    private String score;
    private String ipType;
    private String ipTypeName;
    private GeoipEntity geoipEntity;
    //第三方风险IP
    private ThirdRiskObjs thirdRiskObjs;
    //风险标签 2017-03-23
    private RiskLabelObjs riskLabelObjs;
    //代理IP历史 2018-08-21
    private ProxyHistoryObj proxyHistoryObj;
    //缓存的时间,跟随缓存设置ttl时设置
    private String cacheTime;

    public long getIpl() {
        return ipl;
    }

    public void setIpl(long ipl) {
        this.ipl = ipl;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public String getIpTypeName() {
        return ipTypeName;
    }

    public void setIpTypeName(String ipTypeName) {
        this.ipTypeName = ipTypeName;
    }

    public ThirdRiskObjs getThirdRiskObjs() {
        return thirdRiskObjs;
    }

    public void setThirdRiskObjs(ThirdRiskObjs thirdRiskObjs) {
        this.thirdRiskObjs = thirdRiskObjs;
    }

    public RiskLabelObjs getRiskLabelObjs() {
        return riskLabelObjs;
    }

    public void setRiskLabelObjs(RiskLabelObjs riskLabelObjs) {
        this.riskLabelObjs = riskLabelObjs;
    }

    public String getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(String cacheTime) {
        this.cacheTime = cacheTime;
    }

    public GeoipEntity getGeoipEntity() {
        return geoipEntity;
    }

    public void setGeoipEntity(GeoipEntity geoipEntity) {
        this.geoipEntity = geoipEntity;
    }

    public ProxyHistoryObj getProxyHistoryObj() {
        return proxyHistoryObj;
    }

    public void setProxyHistoryObj(ProxyHistoryObj proxyHistoryObj) {
        this.proxyHistoryObj = proxyHistoryObj;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
