package cn.tongdun.kunpeng.api.intf.ip.entity;

import java.io.Serializable;

/**
 * WhoIsObj
 *
 * @author pandy(潘清剑)
 * @date 16/6/24
 */
public class WhoIsObj extends UpdateTime implements Serializable {

    private Long ip_begin;
    private Long ip_end;
    /**
     * 网段
     */
    private String inetnum;
    /**
     * 网段名
     */
    private String netname;
    /**
     * 描述
     */
    private String descr;
    /**
     * 国家
     */
    private String country;
    /**
     * 变更时间
     */
    private String changed;
    /**
     * 信息来源
     */
    private String source;


    public Long getIp_begin() {
        return ip_begin;
    }

    public void setIp_begin(Long ip_begin) {
        this.ip_begin = ip_begin;
    }

    public Long getIp_end() {
        return ip_end;
    }

    public void setIp_end(Long ip_end) {
        this.ip_end = ip_end;
    }

    public void setInetnum(String inetnum) {
        this.inetnum = inetnum;
    }

    public void setNetname(String netname) {
        this.netname = netname;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setChanged(String changed) {
        this.changed = changed;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInetnum() {
        return inetnum;
    }

    public String getNetname() {
        return netname;
    }

    public String getDescr() {
        return descr;
    }

    public String getCountry() {
        return country;
    }

    public String getChanged() {
        return changed;
    }

    public String getSource() {
        return source;
    }

}
