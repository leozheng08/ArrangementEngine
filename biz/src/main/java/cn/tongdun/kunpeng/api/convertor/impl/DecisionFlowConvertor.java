package cn.tongdun.kunpeng.api.convertor.impl;

import cn.tongdun.kunpeng.api.convertor.DefaultConvertorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:16
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class DecisionFlowConvertor {

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init(){
//        convertorFactory.register(DecisionFlowDO.class,this);
    }
}
