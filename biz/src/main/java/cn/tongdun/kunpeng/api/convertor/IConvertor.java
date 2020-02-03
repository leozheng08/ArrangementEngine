package cn.tongdun.kunpeng.api.convertor;


/**
 * 转换类，由各数据实体转换为运行域的规则、策略、决策树、决策表、决策流实体。
 * @param <T>
 * @param <R>
 */
public interface IConvertor<T,R> {

    R convert(T t);
}
