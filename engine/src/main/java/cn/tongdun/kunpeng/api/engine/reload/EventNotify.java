package cn.tongdun.kunpeng.api.engine.reload;

import cn.tongdun.kunpeng.api.engine.model.UUIDEntity;
import cn.tongdun.kunpeng.api.engine.reload.impl.EventTypeReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyDefinitionReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.PolicyReLoadManager;
import cn.tongdun.kunpeng.api.engine.reload.impl.RuleReLoadManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liang.chen
 * @Date: 2020/3/6 下午2:22
 */
public class EventNotify {


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


    public boolean onMessage(String event){

        DomainEvent domainEvent = eventMsgParser.parse(event);

        for(Object entity : domainEvent.getData()){
            IReload reLoadManager = reloadFactory.getReload(entity.getClass());
            if(domainEvent.getEventType().contains("Remove")){
                reLoadManager.remove(((UUIDEntity)entity).getUuid());
            } else {
                reLoadManager.addOrUpdate(entity);
            }
        }

        return true;
    }
}
