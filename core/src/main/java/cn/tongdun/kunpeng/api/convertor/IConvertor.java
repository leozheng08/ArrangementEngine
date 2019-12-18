package cn.tongdun.kunpeng.api.convertor;


/**
 * 转换类，由各实体转换为可执行的规则、策略、决策树、决策表、决策流。
 * @param <T>
 * @param <R>
 */
public interface IConvertor<T,R> {

    R convert(T t);
}
