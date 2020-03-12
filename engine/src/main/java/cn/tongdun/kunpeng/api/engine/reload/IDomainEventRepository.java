package cn.tongdun.kunpeng.api.engine.reload;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:35
 */
public interface IDomainEventRepository {

    //从缓存中拉取得最新两分钟的变更消息
    List<String> pullLastEventMsgsFromRemoteCache();

    //将领域事件设置到缓存中
    void putEventMsgToRemoteCache(String eventMsg,Long occurredTime);
}
