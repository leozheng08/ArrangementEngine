package cn.tongdun.kunpeng.api.intf.mobile.entity;

import java.io.Serializable;

/**
 * UpdateTime
 *
 * @author pandy(潘清剑)
 * @date 16/7/29
 */
public class UpdateTime implements Serializable{
    private long updateTime = System.currentTimeMillis();

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
