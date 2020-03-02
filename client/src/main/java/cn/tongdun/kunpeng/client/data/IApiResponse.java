package cn.tongdun.kunpeng.client.data;

import java.io.Serializable;

/**
 * @Author: liang.chen
 * @Date: 2020/2/28 下午5:43
 */
public interface IApiResponse extends Serializable {

    boolean isSuccess();

    void setSuccess(boolean success);

    String getReasonCode();

    void setReasonCode(String reasonCode);
}
