package cn.tongdun.kunpeng.api.application.phone.constant;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhoneModelResult implements Serializable {

    private String level;

    private String recommend;

    private Integer score;

}
