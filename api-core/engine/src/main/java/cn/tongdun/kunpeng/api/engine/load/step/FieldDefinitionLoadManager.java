package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 加载系统字段，扩展字段，依赖EventType的加载
 *
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_PARTNER)
public class FieldDefinitionLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(FieldDefinitionLoadManager.class);

    @Autowired
    IFieldDefinitionRepository ruleFieldRepository;

    @Autowired
    FieldDefinitionCache ruleFieldCacheRepository;

    @Autowired
    EventTypeCache eventTypeCacheRepository;

    @Override
    public boolean load(){
        logger.info(TraceUtils.getFormatTrace()+"FieldDefinitionLoadManager start");
        long beginTime = System.currentTimeMillis();

        List<FieldDefinition> list = ruleFieldRepository.queryAllSystemField();
        logger.info("ruleFieldRepository.queryAllSystemField() size:"+ list.size());
        for(FieldDefinition ruleField:list){
            ruleFieldCacheRepository.put(ruleField.getUuid(),ruleField);
        }

        list = ruleFieldRepository.queryAllExtendField();
        for(FieldDefinition ruleField:list){
            ruleFieldCacheRepository.put(ruleField.getUuid(),ruleField);
        }

        logger.info(TraceUtils.getFormatTrace()+"FieldDefinitionLoadManager success, cost:{}, systemFieldMap size:{}, extendFieldMap size:{}",
                System.currentTimeMillis() - beginTime, ruleFieldCacheRepository.getSystemFieldMap().size(),ruleFieldCacheRepository.getExtendFieldMap().size());

        logger.info("ruleFieldCacheRepository.getSystemFieldMap().size():"+ruleFieldCacheRepository.getSystemFieldMap().size());
        return true;
    }
}
