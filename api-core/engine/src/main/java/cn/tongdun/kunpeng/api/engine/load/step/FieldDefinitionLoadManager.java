package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.eventtype.EventTypeCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    IFieldDefinitionRepository fieldDefinitionRepository;

    @Autowired
    FieldDefinitionCache fieldDefinitionCache;

    @Autowired
    EventTypeCache eventTypeCacheRepository;

    @Override
    public boolean load() {
        logger.info(TraceUtils.getFormatTrace() + "FieldDefinitionLoadManager start");
        long beginTime = System.currentTimeMillis();

        List<FieldDefinition> list = fieldDefinitionRepository.queryAllSystemField();
        logger.info("ruleFieldRepository.queryAllSystemField() size:" + list.size());
        for (FieldDefinition ruleField : list) {
            fieldDefinitionCache.put(ruleField.getUuid(), ruleField);
        }

        list = fieldDefinitionRepository.queryAllExtendField();
        for (FieldDefinition ruleField : list) {
            fieldDefinitionCache.put(ruleField.getUuid(), ruleField);
        }

        logger.info(TraceUtils.getFormatTrace() + "FieldDefinitionLoadManager success, cost:{}, systemFieldMap size:{}, extendFieldMap size:{}",
                System.currentTimeMillis() - beginTime, fieldDefinitionCache.getSystemFieldMap().size(), fieldDefinitionCache.getExtendFieldMap().size());

        return true;
    }
}
