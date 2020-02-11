package cn.tongdun.kunpeng.api.engine.convertor.convertor;

public interface IConvertorFactory {

    IConvertor getConvertor(Class clazz);
}
