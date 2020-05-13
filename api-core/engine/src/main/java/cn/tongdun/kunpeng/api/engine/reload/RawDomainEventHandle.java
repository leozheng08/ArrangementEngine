package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.ddd.common.domain.CommonEntity;
import cn.tongdun.kunpeng.api.acl.event.notice.IDomainEventRepository;
import cn.tongdun.kunpeng.api.acl.event.notice.IRawDomainEventHandle;
import cn.tongdun.kunpeng.api.common.data.DomainEventTypeEnum;
import cn.tongdun.kunpeng.api.common.util.TimestampUtil;
import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueKVRepository;
import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.ICustomListValueRepository;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.CustomListValueEventDO;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.engine.reload.docache.DataObjectCacheFactory;
import cn.tongdun.kunpeng.api.engine.reload.docache.IDataObjectCache;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
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

    @Autowired
    private DataObjectCacheFactory dataObjectCacheFactory;

    /**
     * 接收到kunpeng-admin的原始消息。将这些消息写到redis或aerospike远程缓存中
     */
    @Override
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
            logger.error(TraceUtils.getFormatTrace()+"handleRawMessage error",e);
            throw e;
        }
    }


    private void putCustomListValueToRemoteCache(Map rawEventMsg){
        try {
            DomainEvent<CustomListValueEventDO> domainEvent = eventMsgParser.parse(rawEventMsg);
            List<CustomListValueEventDO> listValueDOList = domainEvent.getData();

            if (listValueDOList == null || listValueDOList.isEmpty()) {
                logger.debug(TraceUtils.getFormatTrace()+"CustomListValue put remote cache error,list is empty:{}", rawEventMsg);
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
                    logger.debug(TraceUtils.getFormatTrace()+"CustomListValue put remote cache success,list uuid:{} data:{}",customListValue.getCustomListUuid(), customListValue.getValue());
                } else {
                    customListValueKVRepository.removeCustomListValueData(customListValue);
                    logger.debug(TraceUtils.getFormatTrace()+"CustomListValue remove remote cache success,list uuid:{} data:{}",customListValue.getCustomListUuid(), customListValue.getValue());
                }
            }
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"CustomListValue put remote cache error,event:{}",rawEventMsg,e);
            throw e;
        }
    }

    //将领域事件保存到redis，供kunpeng-api每个主机从redis中拉取事件列表
    private void putEventMsgToRemoteCache(Map rawEventMsg){
        cacheDataObject(rawEventMsg);
        domainEventRepository.putEventMsgToRemoteCache(JSON.toJSONString(rawEventMsg),JsonUtil.getLong(rawEventMsg,"occurredTime"));
    }


    //缓存数据对象
    private void cacheDataObject(Map rawEventMsg){
        String eventType = JsonUtil.getString(rawEventMsg,"eventType");
        String entryName = JsonUtil.getString(rawEventMsg,"entity");
        Long occurredTime = JsonUtil.getLong(rawEventMsg,"occurredTime");
        if(StringUtils.isBlank(eventType) || StringUtils.isBlank(entryName) || occurredTime == null){
            return;
        }
        IDataObjectCache dataObjectCache = dataObjectCacheFactory.getDOCacheByName(entryName);
        if(dataObjectCache == null){
            return;
        }

        List<Map> list = (List)rawEventMsg.get("data");
        if(list == null || list.isEmpty()){
            return;
        }
        //强制从数据库中查询
        ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_FORCE_FROM_DB,true);


        for(Map map:list){
            String uuid = (String)map.get("uuid");
            CommonEntity cachedEntity = dataObjectCache.get(uuid);
            if (eventType.toUpperCase().endsWith(DomainEventTypeEnum.DEACTIVATE.name())
                    || eventType.toUpperCase().endsWith(DomainEventTypeEnum.REMOVE.name())){
                //删除缓存数据
                dataObjectCache.remove(uuid);
            } else {
                if(cachedEntity != null && TimestampUtil.compare(cachedEntity.getGmtModify().getTime(), occurredTime)==1){
                    //缓存中的数据为最新的，则不需要刷新缓存
                    continue;
                }
                //刷新数据
                dataObjectCache.refresh(uuid);
            }
        }
    }
}
