package cn.tongdun.kunpeng.api.dataobject;


import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@Data
public class PolicyDO extends CommonDTO {

    private static final long serialVersionUID = 1456486484654131346L;

    private String                  partnerCode;
    private String                  appName;
    private String                  eventId;
    private String                  version;
    private List<SubPolicyDO>       subPolicyList;

    /**
     * 合作方
     */
    private String partnerCode;

    /**
     * 状态
     * 0：已关闭
     * 1：已启用
     */

    private Integer status;

    /**
     * 是否删除
     */
    private boolean deleted;

    /**
     * 删除时间
     */
    private Date gmtDelete;

    /**
     * 排序
     */
    private Integer displayOrder;

    /**
     * 是否模板
     */
    private boolean template;

    /**
     * 版本
     */
    private String version;

    /**
     * 版本描述
     */
    private String versionDesc;

    /**
     * 是否默认版本
     */
    private boolean defaultVersion;

    /**
     * 扩展字段, kv结构
     */
    private String attribute;

    /**
     * 决策方式
     * 并行、决策流、决策树、决策表等
     * parallel、flow、tree、table
     */
    private String currDecisionMode;

    /**
     * 决策方式对应的策略工具uuid
     * 当currDecisionMode=parallel是，currDecisionModeUuid=null
     */
    private String currDecisionModeUuid;

    /**
     * 策略定义uuid
     */
    private String policyDefinitionUuid;

    /**
     * 原模板uuid（策略uuid）
     */
    private String originTemplateUuid;
}
