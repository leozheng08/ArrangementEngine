package cn.tongdun.kunpeng.api.intf.ip.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * ProxyHistoryObj
 * 代理历史
 *
 * @author pandy(潘清剑)
 * @date 16/7/29
 */
public class ProxyHistoryObj extends UpdateTime implements Serializable {

    private List<ProxyObj> proxyObjs;

    public List<ProxyObj> getProxyObjs() {
        return proxyObjs;
    }

    public void setProxyObjs(List<ProxyObj> proxyObjs) {
        this.proxyObjs = proxyObjs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
