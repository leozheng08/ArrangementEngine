package cn.tongdun.kunpeng.api.runtime;

import cn.tongdun.kunpeng.api.context.FraudContext;
import cn.tongdun.kunpeng.common.data.Response;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午1:47
 */
public interface IExecutor<T,R> {

  R execute(T key, FraudContext context);
}
