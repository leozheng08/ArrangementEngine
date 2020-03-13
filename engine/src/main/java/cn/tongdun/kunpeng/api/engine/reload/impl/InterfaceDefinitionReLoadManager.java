package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.intfdefinition.IInterfaceDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinition;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinitionCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.InterfaceDefinitionDO;
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
public class InterfaceDefinitionReLoadManager implements IReload<InterfaceDefinitionDO> {

    private Logger logger = LoggerFactory.getLogger(InterfaceDefinitionReLoadManager.class);

    @Autowired
    private IInterfaceDefinitionRepository interfaceDefinitionRepository;

    @Autowired
    private InterfaceDefinitionCache interfaceDefinitionCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(InterfaceDefinitionDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(InterfaceDefinitionDO interfaceDefinitionDO){
        String uuid = interfaceDefinitionDO.getUuid();
        logger.debug("InterfaceReLoadManager start, uuid:{}",uuid);
        try {
            Long timestamp = interfaceDefinitionDO.getGmtModify().getTime();
            InterfaceDefinition interfaceDefinition = interfaceDefinitionCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && interfaceDefinition != null && interfaceDefinition.getModifiedVersion() >= timestamp) {
                return true;
            }

            InterfaceDefinition newInterfaceDefinition = interfaceDefinitionRepository.queryByUuid(uuid);
            if(newInterfaceDefinition == null){
                return true;
            }
            interfaceDefinitionCache.put(uuid, newInterfaceDefinition);
        } catch (Exception e){
            logger.error("InterfaceReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("InterfaceReLoadManager success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param interfaceDefinitionDO
     * @return
     */
    @Override
    public boolean remove(InterfaceDefinitionDO interfaceDefinitionDO){
        try {
            interfaceDefinitionCache.remove(interfaceDefinitionDO.getUuid());
        } catch (Exception e){
            return false;
        }
        return true;
    }



}
