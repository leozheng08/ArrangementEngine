package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.constant.ReloadConstant;
import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.IInterfaceDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinition;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinitionCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.InterfaceDefinitionEventDO;
import cn.tongdun.tdframework.core.concurrent.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
public class InterfaceDefinitionReLoadManager implements IReload<InterfaceDefinitionEventDO> {

    private Logger logger = LoggerFactory.getLogger(InterfaceDefinitionReLoadManager.class);

    @Autowired
    private IInterfaceDefinitionRepository interfaceDefinitionRepository;

    @Autowired
    private InterfaceDefinitionCache interfaceDefinitionCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(InterfaceDefinitionEventDO.class,this);
    }

    @Override
    public boolean create(InterfaceDefinitionEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean update(InterfaceDefinitionEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean activate(InterfaceDefinitionEventDO eventDO){
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(InterfaceDefinitionEventDO eventDO){
        String uuid = eventDO.getUuid();
        logger.debug("InterfaceDefinition reload start, uuid:{}",uuid);
        try {
            Long timestamp = eventDO.getGmtModify().getTime();
            InterfaceDefinition interfaceDefinition = interfaceDefinitionCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && interfaceDefinition != null && timestampCompare(interfaceDefinition.getModifiedVersion(), timestamp) >= 0) {
                logger.debug("InterfaceDefinition reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            //设置要查询的时间戳，如果redis缓存的时间戳比这新，则直接按redis缓存的数据返回
            ThreadContext.getContext().setAttr(ReloadConstant.THREAD_CONTEXT_ATTR_MODIFIED_VERSION,timestamp);
            InterfaceDefinition newInterfaceDefinition = interfaceDefinitionRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if(newInterfaceDefinition == null || !newInterfaceDefinition.isValid()){
                return remove(eventDO);
            }

            interfaceDefinitionCache.put(uuid, newInterfaceDefinition);
        } catch (Exception e){
            logger.error("InterfaceDefinition reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("InterfaceDefinition reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(InterfaceDefinitionEventDO eventDO){
        try {
            interfaceDefinitionCache.remove(eventDO.getUuid());
        } catch (Exception e){
            logger.error("InterfaceDefinition remove failed, uuid:{}",eventDO.getUuid(),e);
            return false;
        }
        logger.debug("InterfaceDefinition remove success, uuid:{}",eventDO.getUuid());
        return true;
    }



    /**
     * 关闭状态
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(InterfaceDefinitionEventDO eventDO){
        return remove(eventDO);
    }


}
