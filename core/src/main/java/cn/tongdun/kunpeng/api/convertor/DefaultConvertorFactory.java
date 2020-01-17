package cn.tongdun.kunpeng.api.convertor;

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
