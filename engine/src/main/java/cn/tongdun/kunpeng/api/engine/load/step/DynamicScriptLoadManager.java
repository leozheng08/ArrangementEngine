package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.IInterfaceDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinition;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinitionCache;
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
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_PARTNER)
public class DynamicScriptLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(DynamicScriptLoadManager.class);

//
//    @Autowired
//    IDynamicScriptRepository interfaceDefinitionRepository;
//
//    @Autowired
//    DynamicScriptCache interfaceDefinitionCache;

    @Override
    public boolean load(){
        logger.info("DynamicScriptLoadManager start");

//        List<InterfaceDefinition> list = interfaceDefinitionRepository.queryAllAvailable();
//        if(list == null || list.isEmpty()) {
//            logger.info("InterfaceDefinitionLoadManager warn: InterfaceDefinition is empty");
//            return true;
//        }
//
//        for(InterfaceDefinition interfaceDefinition:list) {
//            interfaceDefinitionCache.put(interfaceDefinition.getUuid(),interfaceDefinition);
//        }
//        logger.info("DynamicScriptLoadManager success, size:"+list.size());
        return true;
    }
}
