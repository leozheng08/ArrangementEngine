package cn.tongdun.kunpeng.api.engine;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午1:47
 */
public interface IExecutor<T,R> {

  R execute(T key, AbstractFraudContext context);
}
