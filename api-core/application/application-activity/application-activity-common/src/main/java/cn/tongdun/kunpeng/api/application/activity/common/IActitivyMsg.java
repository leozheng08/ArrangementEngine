package cn.tongdun.kunpeng.api.application.activity.common;


public interface IActitivyMsg {


    /**
     * 消息的key
     * @return
     */
    String getMessageKey();

    /**
     * 生成activity消息
     * @return
     */
    String toJsonString();
}
