package cn.tongdun.kunpeng.api.acl.api.domain.event.notice;

import java.util.List;

/**
 * 从缓存或储存中拉取近几分钟的领域事件
 * 实现可以是redis或zk
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:35
 */
public interface IDomainEventRepository {

    //从缓存中拉取得最新两分钟的变更消息
    List<String> pullLastEventMsgsFromRemoteCache();

    //将领域事件设置到缓存中
    void putEventMsgToRemoteCache(String eventMsg,Long occurredTime);
}
