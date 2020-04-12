package cn.tongdun.kunpeng.api.application.activity;

/**
 * @Author: liang.chen
 * @Date: 2020/3/5 下午5:24
 */
public interface IMsgProducer {
    /**
     * 消息发送
     * @param message
     */
    void produce(final String topic, final String messageKey, final String message) ;
}
