package cn.tongdun.kunpeng.api.application.activity.common;

import cn.tongdun.kunpeng.api.common.data.QueueItem;

import java.util.function.Predicate;

/**
 *
 * @Author: liang.chen
 * @Date: 2020/3/3 下午7:28
 */
public interface IEventWorker {

    //事件处理
    void onEvent(QueueItem item);

    //名称
    String getName();

    //过滤条件
    Predicate<QueueItem> getFilter();
}
