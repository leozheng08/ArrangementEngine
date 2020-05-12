package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_COMM)
public class EventTypeLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(EventTypeLoadManager.class);

    @Autowired
    IEventTypeRepository eventTypeRepository;

    @Autowired
    EventTypeCache eventTypeLocalCache;

    @Override
    public boolean load(){
        logger.info("EventTypeLoadManager start");
        long beginTime = System.currentTimeMillis();
        List<EventType> list = eventTypeRepository.queryAll();
        if(list != null) {
            for(EventType eventType :list) {
                eventTypeLocalCache.put(eventType.getUuid(),eventType);
            }
        }
        logger.info("EventTypeLoadManager success, cost:{}, size:{}",
                System.currentTimeMillis() - beginTime, eventTypeLocalCache.getEventTypes().size());
        return true;
    }
}
