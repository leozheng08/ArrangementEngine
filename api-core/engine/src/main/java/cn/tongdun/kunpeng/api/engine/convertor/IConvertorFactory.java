package cn.tongdun.kunpeng.api.engine.convertor;

public interface IConvertorFactory {

    IConvertor getConvertor(Class clazz);
}
