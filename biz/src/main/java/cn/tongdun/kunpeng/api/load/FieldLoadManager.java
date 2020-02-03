package cn.tongdun.kunpeng.api.load;

import cn.tongdun.kunpeng.api.core.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.core.field.FieldDefinition;
import cn.tongdun.kunpeng.api.core.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.api.core.field.FieldDefinitionCache;
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
public class FieldLoadManager implements ILoad{

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
        FieldDefinition params = new FieldDefinition();
        //0为系统字段 1为扩展字段
        params.setSign(0);
        List<FieldDefinition> list = ruleFieldRepository.queryByParams(params);
        for(FieldDefinition ruleField:list){
            ruleFieldCacheRepository.addSystemField(ruleField,eventTypeCacheRepository.getEventTypeList());
        }


        params.setSign(1);
        list = ruleFieldRepository.queryByParams(params);
        for(FieldDefinition ruleField:list){
            ruleFieldCacheRepository.addExtendField(ruleField,eventTypeCacheRepository.getEventTypeList());
        }
        logger.info("FieldLoadManager load() success,systemFieldMap:"+ruleFieldCacheRepository.getSystemFieldMap().size()+" extendFieldMap:"+ruleFieldCacheRepository.getExtendFieldMap().size());
        return true;
    }
}
