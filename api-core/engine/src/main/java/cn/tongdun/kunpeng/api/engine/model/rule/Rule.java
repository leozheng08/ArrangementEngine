package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.tongdun.kunpeng.api.engine.convertor.batch.AbstractBatchRemoteCallData;
import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import cn.tongdun.kunpeng.api.engine.model.rule.function.WeightFunction;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 规则实体，规则的定义已经经过解析，可放到manager中执行。
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:20
 */
@Data
public class Rule extends VersionedEntity {

    private String ruleId;
    private String ruleCustomId;
    private String parentUuid;
    private String bizType;
    private String bizUuid;

    private String name;
    private String mode;
    //决策结果
    private String decision;
    private Integer displayOrder;
    private Boolean pilotRun;
    private String template;
    /**
     * 规则引擎的可执行对象
     */
    private cn.fraudmetrix.module.tdrule.rule.Rule eval;
    /**
     * 定义一个tdrule的函数来计算规则的权重
     */
    private WeightFunction weightFunction;

    /**
     * 需要批量远程调用的数据
     * 数据结构：<policyuuid,<template,List<AbstractBatchRemoteCallData>>>
     * @see AbstractBatchRemoteCallData
     */
    private Map<String,Map<String,List<Object>>> batchRemoteCallData;

}
