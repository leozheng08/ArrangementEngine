package cn.tongdun.kunpeng.api.intf.ip.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * ProxyObj
 *
 * @author pandy(潘清剑)
 * @date 16/6/24
 */
public class ProxyObj implements Serializable {
    /**
     * 端口号
     */
    private String port;
    /**
     * 代理类型
     */
    private String type;
    /**
     * 最先出现时间
     */
    private String firstTime;
    /**
     * 最后发现时间
     */
    private String lastTime;
    /**
     * 来源
     */
    private String source;

    private String updateTime;

    private String typeFapi;

    public String getTypeFapi() {
        return typeFapi;
    }

    public void setTypeFapi(String typeFapi) {
        this.typeFapi = typeFapi;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
