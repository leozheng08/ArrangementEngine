package cn.tongdun.kunpeng.api.intf.ip.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * IpReputationObj
 *  ip画像
 * @author pandy(潘清剑)
 * @date 16/6/28
 */
public class IpReputationObj extends UpdateTime implements Serializable {
    private String ip;

    private IpBsIdcObj ipBsIdcObj;

    private ProxyHistoryObj proxyHistoryObj;

    /**
     * whois 信息
     */
    private WhoIsObj whoIsObj;

    /**
     * 域名历史信息
     */
    private PassiveDnsObjs passiveDnsObjs;
    /**
     * 第三方风险事件信息
     */
    private ThirdRiskObjs thirdRiskObjs;
    /**
     * 线上风险事件
     */
    private OnLineRiskObjs onLineRiskObjs;

    /**
     * IP位置
     */
    private LocationObj locationObj;

    //缓存的时间,跟随缓存设置ttl时设置
    private String cacheTime;

    public OnLineRiskObjs getOnLineRiskObjs() {
        return onLineRiskObjs;
    }

    public void setOnLineRiskObjs(OnLineRiskObjs onLineRiskObjs) {
        this.onLineRiskObjs = onLineRiskObjs;
    }

    public IpBsIdcObj getIpBsIdcObj() {
        return ipBsIdcObj;
    }

    public void setIpBsIdcObj(IpBsIdcObj ipBsIdcObj) {
        this.ipBsIdcObj = ipBsIdcObj;
    }

    public ProxyHistoryObj getProxyHistoryObj() {
        return proxyHistoryObj;
    }

    public void setProxyHistoryObj(ProxyHistoryObj proxyHistoryObj) {
        this.proxyHistoryObj = proxyHistoryObj;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public WhoIsObj getWhoIsObj() {
        return whoIsObj;
    }

    public void setWhoIsObj(WhoIsObj whoIsObj) {
        this.whoIsObj = whoIsObj;
    }

    public PassiveDnsObjs getPassiveDnsObjs() {
        return passiveDnsObjs;
    }

    public void setPassiveDnsObjs(PassiveDnsObjs passiveDnsObjs) {
        this.passiveDnsObjs = passiveDnsObjs;
    }

    public ThirdRiskObjs getThirdRiskObjs() {
        return thirdRiskObjs;
    }

    public void setThirdRiskObjs(ThirdRiskObjs thirdRiskObjs) {
        this.thirdRiskObjs = thirdRiskObjs;
    }

    public LocationObj getLocationObj() {
        return locationObj;
    }

    public void setLocationObj(LocationObj locationObj) {
        this.locationObj = locationObj;
    }

    public String getCacheTime() {
        return cacheTime;
    }

    public void setCacheTime(String cacheTime) {
        this.cacheTime = cacheTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
