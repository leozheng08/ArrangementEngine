package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueKVRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueRepository;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.CustomListValueEventDO;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 对接收kunpeng-admin的领域事件做处理
 * 主要包含：
 * 1. 自定义列表的事件，放到redis缓存中的有序集合中
 * 2. 策略配置相关的事件，按当前分钟为key放到放redis中有序集合中，供kunpeng-api各主机拉取
 * @Author: liang.chen
 * @Date: 2020/3/12 下午1:39
 */
@Component
public class RawDomainEventHandle implements IRawDomainEventHandle{
    private Logger logger = LoggerFactory.getLogger(RawDomainEventHandle.class);


    @Autowired
    private IDomainEventRepository domainEventRepository;

    @Autowired
    private ICustomListValueKVRepository customListValueKVRepository;

    @Autowired
    private ICustomListValueRepository customListValueRepository;

    @Autowired
    private EventMsgParser eventMsgParser;


    /**
     * 接收到kunpeng-admin的原始消息。将这些消息写到redis或aerospike远程缓存中
     */
    public void handleRawMessage(Map rawEventMsg){
        try {
            if (rawEventMsg == null) {
                return;
            }

            String entityName = JsonUtil.getString(rawEventMsg,"entity");
            if (StringUtils.isBlank(entityName)) {
                return;
            }

            switch (entityName.toUpperCase()) {
                case "CUSTOM_LIST_VALUE":
                    putCustomListValueToRemoteCache(rawEventMsg);
                    break;
                default:
                    putEventMsgToRemoteCache(rawEventMsg);
            }
        } catch (Exception e){
            logger.error("handleRawMessage error",e);
            throw e;
        }
    }


    private void putCustomListValueToRemoteCache(Map rawEventMsg){
        try {
            DomainEvent<CustomListValueEventDO> domainEvent = eventMsgParser.parse(rawEventMsg);
            List<CustomListValueEventDO> listValueDOList = domainEvent.getData();

            if (listValueDOList == null || listValueDOList.isEmpty()) {
                logger.debug("CustomListValue put remote cache error,list is empty:{}", rawEventMsg);
                return;
            }

            List<String> uuids = new ArrayList<>();
            listValueDOList.forEach(value -> {
                uuids.add(value.getUuid());
            });
            List<CustomListValue> listValueList = customListValueRepository.selectByUuids(uuids);
            for (CustomListValue customListValue : listValueList) {
                if (customListValue.isValid()) {
                    customListValueKVRepository.putCustomListValueData(customListValue);
                    logger.debug("CustomListValue put remote cache success,list uuid:{} data:{}",customListValue.getCustomListUuid(), customListValue.getValue());
                } else {
                    customListValueKVRepository.removeCustomListValueData(customListValue);
                    logger.debug("CustomListValue remove remote cache success,list uuid:{} data:{}",customListValue.getCustomListUuid(), customListValue.getValue());
                }
            }
        } catch (Exception e){
            logger.error("CustomListValue put remote cache error,event:{}",rawEventMsg,e);
            throw e;
        }
    }

    //将领域事件保存到redis，供kunpeng-api每个主机从redis中拉取事件列表
    private void putEventMsgToRemoteCache(Map rawEventMsg){
        domainEventRepository.putEventMsgToRemoteCache(JSON.toJSONString(rawEventMsg),JsonUtil.getLong(rawEventMsg,"occurredTime"));
    }
}
