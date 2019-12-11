package cn.tongdun.kunpeng.api.load;

import cn.tongdun.kunpeng.api.eventtype.EventType;
import cn.tongdun.kunpeng.api.eventtype.EventTypeCacheRepository;
import cn.tongdun.kunpeng.api.eventtype.IEventTypeRepository;
import cn.tongdun.kunpeng.api.field.IRuleFieldRepository;
import cn.tongdun.kunpeng.api.field.RuleField;
import cn.tongdun.kunpeng.api.field.RuleFieldCacheRepository;
import cn.tongdun.tdframework.core.logger.Logger;
import cn.tongdun.tdframework.core.logger.LoggerFactory;
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
    EventTypeCacheRepository eventTypeCacheRepository;

    @Override
    public boolean load(){
        logger.info("EventTypeLoadManager load()");
        RuleField field = new RuleField();
        //0为系统字段 1为扩展字段
        field.setSign(0);
        List<EventType> list = eventTypeRepository.queryAll();
        if(list != null) {
            eventTypeCacheRepository.setEventTypeList(list);
        }
        logger.info("EventTypeLoadManager load() success"+eventTypeCacheRepository.getEventTypeList().size());
        return true;
    }
}
