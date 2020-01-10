package cn.tongdun.kunpeng.api.intf.ip.entity;

import java.io.Serializable;

/**
 * IpBsIdcObj
 *
 * @author pandy(潘清剑)
 * @date 16/7/28
 */
public class IpBsIdcObj extends UpdateTime implements Serializable{
    /**
     * 是否是基站
     */
    private boolean  isBs;

    /**
     * 是否是Idc
     */
    private boolean  isIdc;


    public boolean isBs() {
        return isBs;
    }

    public void setBs(boolean bs) {
        isBs = bs;
    }

    public boolean isIdc() {
        return isIdc;
    }

    public void setIdc(boolean idc) {
        isIdc = idc;
    }

}
