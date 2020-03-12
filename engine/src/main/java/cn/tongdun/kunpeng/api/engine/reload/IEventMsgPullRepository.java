package cn.tongdun.kunpeng.api.engine.reload;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:35
 */
public interface IEventMsgPullRepository {

    //拉取得最新两分钟的变更消息
    List<String> pullLastEventMsgs();
}
