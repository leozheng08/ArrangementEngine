package cn.tongdun.kunpeng.api.intf.ip.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * RiskHistoryObj
 *
 * @author pandy(潘清剑)
 * @date 16/6/24
 */
public class RiskHistoryObj implements Serializable {

    /**
     attacks	频繁进行网络攻击的IP
     bots	已经被控制的傀儡主机
     scanning	频繁进行网络扫描的IP
     spam	发布垃圾信息、垃圾邮件的IP
     malware	提供恶意程序发布、下载的IP
     c&c	用于控制bots的节点
     annonymous	用于提供匿名服务的开放式代理(这部分数据放到Argus上去做)
     */
    private String  category;
    private String  categoryName;
    /**
     * 时间
     */
    private String  updateTime;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
