package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

import java.util.Date;


/**
 * 策略定义
 */
@Data
public class PolicyDefinitionDTO extends CommonDTO {

    private String name;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件标识
     */
    private String eventId;

    /**
     * 应用
     */
    private String appName;

    /**
     * 应用类型
     */
    private String appType;

    /**
     * 描述
     */
    private String description;

    /**
     * 合作方
     */
    private String partnerCode;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 是否模板
     */
    private boolean template;

    /**
     * 行业类型
     */
    private String industryType;

    /**
     * 产品标签
     */
    private String productTag;


    /**
     * 是否通用策略
     */
    private boolean common;


    /**
     * 当前调用版本
     */
    private String currVersion;
    private String currVersionUuid;

    /**
     * 删除时间
     */
    private Date gmtDelete;



}