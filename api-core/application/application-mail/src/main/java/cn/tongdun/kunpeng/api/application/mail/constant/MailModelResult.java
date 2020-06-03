package cn.tongdun.kunpeng.api.application.mail.constant;

import cn.tongdun.kunpeng.client.dto.CommonDTO;

/**
 * @author: yuanhang
 * @date: 2020-06-03 17:00
 **/
public class MailModelResult extends CommonDTO {

    private Integer status_code;

    private String status_msg;

    private Double time;

    private Integer sim_result;

    private Double rand_result;

    public MailModelResult() {
    }

    public MailModelResult(Integer status_code, String status_msg, Double time, Integer sim_result, Double rand_result) {
        this.status_code = status_code;
        this.status_msg = status_msg;
        this.time = time;
        this.sim_result = sim_result;
        this.rand_result = rand_result;
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

    public Integer getSim_result() {
        return sim_result;
    }

    public void setSim_result(Integer sim_result) {
        this.sim_result = sim_result;
    }

    public Double getRand_result() {
        return rand_result;
    }

    public void setRand_result(Double rand_result) {
        this.rand_result = rand_result;
    }
}
