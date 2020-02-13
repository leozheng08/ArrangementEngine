package cn.tongdun.kunpeng.api.engine.load;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
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
public class FieldDefinitionLoadManager implements ILoad{

    private Logger logger = LoggerFactory.getLogger(PipelineExecutor.class);

    @Autowired
    IFieldDefinitionRepository ruleFieldRepository;

    @Autowired
    FieldDefinitionCache ruleFieldCacheRepository;

    @Autowired
    EventTypeCache eventTypeCacheRepository;

    @Override
    public boolean load(){
        logger.info("FieldLoadManager load()");
        List<FieldDefinition> list = ruleFieldRepository.queryAllSystemField();
        for(FieldDefinition ruleField:list){
            ruleFieldCacheRepository.addSystemField(ruleField,eventTypeCacheRepository.getEventTypeList());
        }

        list = ruleFieldRepository.queryAllExtendField();
        for(FieldDefinition ruleField:list){
            ruleFieldCacheRepository.addExtendField(ruleField,eventTypeCacheRepository.getEventTypeList());
        }
        logger.info("FieldLoadManager load() success,systemFieldMap size:"+ruleFieldCacheRepository.getSystemFieldMap().size()+" extendFieldMap size:"+ruleFieldCacheRepository.getExtendFieldMap().size());
        return true;
    }
}
