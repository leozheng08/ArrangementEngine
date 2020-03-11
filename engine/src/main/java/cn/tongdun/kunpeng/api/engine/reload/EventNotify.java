package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.engine.model.UUIDEntity;
import cn.tongdun.kunpeng.api.engine.reload.impl.EventTypeReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyDefinitionReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.RuleReLoadManager;
import cn.tongdun.tdframework.core.pipeline.PipelineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liang.chen
 * @Date: 2020/3/6 下午2:22
 */
public class EventNotify {

    private Logger logger = LoggerFactory.getLogger(EventNotify.class);

    @Autowired
    private EventMsgParser eventMsgParser;
    @Autowired
    private PolicyDefinitionReLoadManager policyDefinitionReLoadManager;
    @Autowired
    private PolicyReLoadManager policyReLoadManager;
    @Autowired
    private RuleReLoadManager ruleReLoadManager;
    @Autowired
    private EventTypeReLoadManager eventTypeReLoadManager;
    @Autowired
    private ReloadFactory reloadFactory;

    private final static String EVENT_TYPE_REMOVE ="Remove";


    public boolean onMessage(String event){
        try {
            DomainEvent domainEvent = eventMsgParser.parse(event);
            boolean result = true;
            for (Object entity : domainEvent.getData()) {
                IReload reLoadManager = reloadFactory.getReload(entity.getClass());
                if (reLoadManager == null) {
                    result = false;
                    logger.error("EventNotify reLoadManager is null,class:{},event:{}", entity.getClass(), event);
                    break;
                }
                if (domainEvent.getEventType().endsWith(EVENT_TYPE_REMOVE)) {
                    if(!reLoadManager.remove(entity)){
                        result = false;
                    }
                } else {
                    if(reLoadManager.addOrUpdate(entity)){
                        result = false;
                    }
                }
            }
            return result;
        } catch (Exception e){
            logger.error("EventNotify reLoadManager is null,class:{},event:{}", event);
            return false;
        }
    }
}
