package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.engine.model.constant.DomainEventTypeEnum;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueKVRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueRepository;
import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 对接收kunpeng-admin的领域事件做处理
 * 主要包含：
 * 1. 自定义列表的事件，放到redis缓存中的有序集合中
 * 2. 策略配置相关的事件，按当前分钟为key放到放redis中有序集合中，供kunpeng-api各主机拉取
 * @Author: liang.chen
 * @Date: 2020/3/12 下午1:39
 */
@Component
public class RawDomainEventHandle {
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
    public void handleRawMessage(JSONObject rawEventMsg){
        if(rawEventMsg == null){
            return;
        }

        String entityName = rawEventMsg.getString("entity");
        if(StringUtils.isBlank(entityName)){
            return;
        }

        switch (entityName.toUpperCase()) {
            case "CUSTOM_LIST_VALUE":
                putCustomListValueToRemoteCache(rawEventMsg);
                break;
            default:
                putEventMsgToRemoteCache(rawEventMsg);
        }
    }


    private void putCustomListValueToRemoteCache(JSONObject rawEventMsg){
        DomainEvent<CustomListValueDO> domainEvent = eventMsgParser.parse(rawEventMsg);
        List<CustomListValueDO> listValueDOList = domainEvent.getData();

        if(listValueDOList == null || listValueDOList.isEmpty()){
            return;
        }

        if (domainEvent.getEventType().toUpperCase().endsWith(DomainEventTypeEnum.REMOVE.name())) {//删除操作
            for(CustomListValueDO customListValueDO:listValueDOList) {
                customListValueKVRepository.removeCustomListValueData(new CustomListValue(customListValueDO.getCustomListUuid(),customListValueDO.getDataValue()));
            }
        } else {//新增或修改操作

            List<String> uuids = new ArrayList<>();
            listValueDOList.forEach(value->{uuids.add(value.getUuid());});

            List<CustomListValue> listValueList = customListValueRepository.selectByUuids(uuids);
            for(CustomListValue customListValue:listValueList) {
                customListValueKVRepository.putCustomListValueData(customListValue);
            }
        }


    }



    //将领域事件保存到redis，供kunpeng-api每个主机从redis中拉取事件列表
    private void putEventMsgToRemoteCache(JSONObject rawEventMsg){
        domainEventRepository.putEventMsgToRemoteCache(rawEventMsg.toJSONString(),rawEventMsg.getLong("occurredTime"));
    }


}
