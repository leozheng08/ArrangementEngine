package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.FieldDefinitionEventDO;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
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
public class FieldDefinitionReLoadManager implements IReload<FieldDefinitionEventDO> {

    private Logger logger = LoggerFactory.getLogger(FieldDefinitionReLoadManager.class);

    @Autowired
    private IFieldDefinitionRepository fieldDefinitionRepository;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(FieldDefinitionEventDO.class,this);
    }

    @Override
    public boolean create(FieldDefinitionEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean update(FieldDefinitionEventDO eventDO){
        return addOrUpdate(eventDO);
    }
    @Override
    public boolean activate(FieldDefinitionEventDO eventDO){
        return addOrUpdate(eventDO);
    }

    /**
     * 更新事件类型
     * @return
     */
    public boolean addOrUpdate(FieldDefinitionEventDO eventDO){
        String uuid = eventDO.getUuid();
        logger.debug("FieldDefinition reload start, uuid:{}",uuid);
        try {
            Long timestamp = eventDO.getModifiedVersion();
            FieldDefinition oldFieldDefinition = (FieldDefinition) fieldDefinitionCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(timestamp != null && oldFieldDefinition != null && oldFieldDefinition.getModifiedVersion() >= timestamp) {
                logger.debug("FieldDefinition reload localCache is newest, ignore uuid:{}",uuid);
                return true;
            }

            FieldDefinition newFieldDefinition = fieldDefinitionRepository.queryByUuid(uuid);
            //如果失效则删除缓存
            if(newFieldDefinition == null || !newFieldDefinition.isValid()){
                return remove(eventDO);
            }

            fieldDefinitionCache.put(uuid, newFieldDefinition);
        } catch (Exception e){
            logger.error("FieldDefinition reload failed, uuid:{}",uuid,e);
            return false;
        }
        logger.debug("FieldDefinition reload success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param eventDO
     * @return
     */
    @Override
    public boolean remove(FieldDefinitionEventDO eventDO){
        try {
            fieldDefinitionCache.remove(eventDO.getUuid());
        } catch (Exception e){
            logger.error("FieldDefinition remove failed, uuid:{}",eventDO.getUuid(),e);
            return false;
        }
        logger.debug("FieldDefinition remove failed, uuid:{}",eventDO.getUuid());
        return true;
    }


    /**
     * 关闭状态
     * @param eventDO
     * @return
     */
    @Override
    public boolean deactivate(FieldDefinitionEventDO eventDO){
        return remove(eventDO);
    }
}
