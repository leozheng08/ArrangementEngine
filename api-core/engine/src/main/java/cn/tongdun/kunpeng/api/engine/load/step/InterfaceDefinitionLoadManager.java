package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.IInterfaceDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinition;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinitionCache;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_COMM)
public class InterfaceDefinitionLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(InterfaceDefinitionLoadManager.class);


    @Autowired
    IInterfaceDefinitionRepository interfaceDefinitionRepository;

    @Autowired
    InterfaceDefinitionCache interfaceDefinitionCache;

    @Override
    public boolean load(){
        logger.info(TraceUtils.getFormatTrace()+"InterfaceDefinitionLoadManager start");
        long beginTime = System.currentTimeMillis();

        List<InterfaceDefinition> list = interfaceDefinitionRepository.queryAllAvailable();
        if(list == null || list.isEmpty()) {
            logger.info(TraceUtils.getFormatTrace()+"InterfaceDefinitionLoadManager warn: InterfaceDefinition is empty");
            return true;
        }

        for(InterfaceDefinition interfaceDefinition:list) {
            interfaceDefinitionCache.put(interfaceDefinition.getUuid(),interfaceDefinition);
        }
        logger.info(TraceUtils.getFormatTrace()+"InterfaceDefinitionLoadManager success, cost:{}, size:{}",
                System.currentTimeMillis() - beginTime, list.size());
        return true;
    }
}
