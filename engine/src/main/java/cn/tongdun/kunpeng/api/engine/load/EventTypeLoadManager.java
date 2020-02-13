package cn.tongdun.kunpeng.api.engine.load;

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
public class EventTypeLoadManager implements ILoad{

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    @Autowired
    IEventTypeRepository eventTypeRepository;

    @Autowired
    EventTypeCache eventTypeLocalCache;

    @Override
    public boolean load(){
        logger.info("EventTypeLoadManager load()");
        FieldDefinition field = new FieldDefinition();
        //0为系统字段 1为扩展字段
        field.setSign(0);
        List<EventType> list = eventTypeRepository.queryAll();
        if(list != null) {
            eventTypeLocalCache.setEventTypeList(list);
        }
        logger.info("EventTypeLoadManager load() success,eventType size:"+eventTypeLocalCache.getEventTypeList().size());
        return true;
    }
}
