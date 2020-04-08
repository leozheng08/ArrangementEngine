package cn.tongdun.kunpeng.api.acl.api.event.notice;

import java.util.Map;

/**
 * 对接收kunpeng-admin的领域事件做处理
 * 主要包含：
 * 1. 自定义列表的事件，放到redis缓存中的有序集合中
 * 2. 策略配置相关的事件，按当前分钟为key放到放redis中有序集合中，供kunpeng-api各主机拉取
 * @Author: liang.chen
 * @Date: 2020/3/12 下午1:39
 */
public interface IRawDomainEventHandle{

    /**
     * 接收到kunpeng-admin的原始消息。将这些消息写到redis或aerospike远程缓存中
     */
    void handleRawMessage(Map rawEventMsg);

}
