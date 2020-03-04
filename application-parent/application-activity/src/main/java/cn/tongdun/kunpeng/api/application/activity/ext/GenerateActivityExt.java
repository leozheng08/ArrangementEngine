package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.tongdun.kunpeng.api.application.activity.IActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.IGenerateActivityExtPt;
import cn.tongdun.kunpeng.common.data.QueueItem;

/**
 * @Author: liang.chen
 * @Date: 2020/3/4 下午4:00
 */
public class GenerateActivityExt implements IGenerateActivityExtPt{

    /**
     * 根据出入参、上下文生成Activity消息
     * @param queueItem
     * @return
     */
    public IActitivyMsg generateActivity(QueueItem queueItem){
        return null;
    }
}
