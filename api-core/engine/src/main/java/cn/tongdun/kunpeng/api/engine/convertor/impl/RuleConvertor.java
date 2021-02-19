package cn.tongdun.kunpeng.api.engine.convertor.impl;


import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilder;
import cn.tongdun.kunpeng.api.engine.convertor.batch.BatchRemoteCallDataBuilderFactory;
import cn.tongdun.kunpeng.api.engine.convertor.rule.CustomRuleBuilder;
import cn.tongdun.kunpeng.api.engine.convertor.rule.FunctionRuleBuilder;
import cn.tongdun.kunpeng.api.engine.convertor.rule.RuleBuilder;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:15
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class RuleConvertor implements IConvertor<RuleDTO, Rule> {

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    private Map<String, String> operatorMap = new HashMap<>();

    private Map<String, String> funcationMap = new HashMap<>();

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init() {
        convertorFactory.register(RuleDTO.class, this);
    }


    @Override
    public Rule convert(RuleDTO dto) {
        try {
            if (null == dto) {
                return null;
            }
            RuleBuilder ruleBuilder = null;
            if (StringUtils.equalsIgnoreCase(dto.getTemplate(), "common/custom")) {
                ruleBuilder = new CustomRuleBuilder();
            } else {
                ruleBuilder = new FunctionRuleBuilder();
            }
            return ruleBuilder.build(dto);
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"RuleConvertor error, ruleUuid:{}",
                    dto.getUuid(),
                    e);
            throw e;
        }
    }

}
