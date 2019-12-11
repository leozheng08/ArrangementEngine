package cn.tongdun.kunpeng.api.load;

import cn.tongdun.tdframework.core.pipeline.IStep;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午4:30
 */
public interface ILoad extends IStep {
    boolean load();
}
