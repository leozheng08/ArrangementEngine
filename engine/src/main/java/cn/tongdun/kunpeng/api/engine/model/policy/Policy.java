package cn.tongdun.kunpeng.api.engine.model.policy;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.AbstractDecisionMode;
import lombok.Data;

import java.util.List;

/**
 * 策略。将会放到缓存中，属性尽量简化，不要保留跟策略运行无关的属性
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class Policy extends VersionedEntity {

    /**
     * 策略名称
     */
    private String            name;

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
     * appName
     */
    private String            appName;

    /**
     * appType
     */
    private String            appType;


    /**
     * 版本
     */
    private String version;

    /**
     * 是否默认版本
     */
    private boolean defaultVersion;

    /**
     * 决策方式 并行、决策流、决策树、决策表等 parallel、flow、tree、table
     */
    private String currDecisionMode;


    /**
     * 决策方式对应的策略工具uuid 当currDecisionMode=parallel是，currDecisionModeUuid=null
     */
    private String currDecisionModeUuid;


    /**
     * 策略定义uuid
     */
    private String policyDefinitionUuid;

    /**
     * 状态 0：已关闭 1：已启用
     */
    private Integer status;

    private boolean deleted;

    //执行方式，并行执行子策略、决策流、决策表、决策树
    private AbstractDecisionMode decisionMode;

}
