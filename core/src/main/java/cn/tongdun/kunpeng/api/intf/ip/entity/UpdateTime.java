package cn.tongdun.kunpeng.api.intf.ip.entity;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * UpdateTime
 *
 * @author pandy(潘清剑)
 * @date 16/7/29
 */
public class UpdateTime implements Serializable{
    private String updateTime = timeTrans(System.currentTimeMillis());

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }


    /**
     * 时间格式转换
     *
     * @param time
     * @return
     */
    public static String timeTrans(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
