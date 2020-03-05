package cn.tongdun.kunpeng.api.engine.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PolicyModifiedDTO {

    /**
     * 策略uuid
     */
    private String            policyUuid;
    /**
     * 合作方编码
     */
    private String            partnerCode;


    /**
     *  eventId
     */
    private String            eventId;

    /**
     * eventType
     */
    private String            eventType;

    /**
     * 版本
     */
    private String version;

    /**
     * 是否默认版本
     */
    private boolean defaultVersion;


    /**
     * 状态 0：已关闭 1：已启用
     */
    private Integer status;


    /**
     * 修改时间
     */
    private Date gmtModify;



}
