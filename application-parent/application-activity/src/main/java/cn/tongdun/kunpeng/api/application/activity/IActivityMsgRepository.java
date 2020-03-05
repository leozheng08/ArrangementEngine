package cn.tongdun.kunpeng.api.application.activity;

/**
 * @Author: liang.chen
 * @Date: 2020/3/5 下午5:24
 */
public interface IActivityMsgRepository {
    /**
     * 消息发送
     * @param messageKey
     * @param message
     */
    void sendRawActivity(String messageKey, final String message) ;
}
