package cn.tongdun.kunpeng.api.convertor;

import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.fraudmetrix.module.tdrule.parser.conditionbuilder.*;
import cn.tongdun.kunpeng.api.convertor.impl.PolicyConvertor;
import cn.tongdun.kunpeng.api.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.api.convertor.impl.SubPolicyConvertor;
import cn.tongdun.kunpeng.api.dataobject.PolicyDO;
import cn.tongdun.kunpeng.api.dataobject.RuleDO;
import cn.tongdun.kunpeng.api.dataobject.SubPolicyDO;
import cn.tongdun.kunpeng.api.subpolicy.SubPolicy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2019/12/10 4:49 PM
 */
@Component(value = "defaultConvertorFactory" )
public class DefaultConvertorFactory implements IConvertorFactory{

    private Map<Class, IConvertor> convertorMap = new HashMap<>(10);

    @Override
    public IConvertor getConvertor(Class clazz) {
        return convertorMap.get(clazz);
    }

    public void register(Class clazz,IConvertor convertor){
        convertorMap.put(clazz, convertor);
    }
}
