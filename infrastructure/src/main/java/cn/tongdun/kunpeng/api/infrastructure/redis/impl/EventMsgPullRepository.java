package cn.tongdun.kunpeng.api.infrastructure.redis.impl;

import cn.tongdun.kunpeng.api.engine.reload.IEventMsgPullRepository;
import cn.tongdun.kunpeng.common.util.DateUtil;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/3/11 下午6:42
 */
public class EventMsgPullRepository implements IEventMsgPullRepository{

    @Override
    public List<String> pullLastEventMsgs(){
        String currentKey = DateUtil.getYYYYMMDDHHMMStr();
        String lastKey = DateUtil.getLastMinute();
        return null;
    }
}
