package cn.tongdun.kunpeng.api.convertor;

public interface IConvertorFactory {

    IConvertor getConvertor(Class clazz);
}
