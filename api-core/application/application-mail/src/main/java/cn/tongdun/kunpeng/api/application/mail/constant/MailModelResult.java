package cn.tongdun.kunpeng.api.application.mail.constant;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * @author: yuanhang
 * @date: 2020-06-03 17:00
 **/
@JsonSerialize
public class MailModelResult implements Serializable {

    private Integer status_code;

    private String status_msg;

    private Double time;

    private Object result;

    private Double ranResult;

    public MailModelResult() {
    }

    public MailModelResult(Integer status_code, String status_msg, Double time, Object result) {
        this.status_code = status_code;
        this.status_msg = status_msg;
        this.time = time;
        this.result = result;
    }

    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Double getRanResult() {
        return ranResult;
    }

    public void setRanResult(Double ranResult) {
        this.ranResult = ranResult;
    }

    @Override
    public String toString() {
        return "MailModelResult{" +
                "status_code=" + status_code +
                ", status_msg='" + status_msg + '\'' +
                ", time=" + time +
                ", result=" + result +
                ", ranResult=" + ranResult +
                '}';
    }
}
