package cn.tongdun.kunpeng.api.application.platformindex.pojo;


import java.io.Serializable;

/**
 * 指标平台 api接口返回信息
 * @author jie
 * @date 2020/12/15
 */
public class IndicatrixApiResult<T> implements Serializable {

    private Integer code;

    private boolean success;

    private String message;

    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
