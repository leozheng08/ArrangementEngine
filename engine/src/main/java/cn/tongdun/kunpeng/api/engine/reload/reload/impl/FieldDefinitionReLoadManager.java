package cn.tongdun.kunpeng.api.engine.reload.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.reload.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.dataobject.FieldDefinitionDO;
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
public class FieldDefinitionReLoadManager implements IReload<FieldDefinitionDO> {

    private Logger logger = LoggerFactory.getLogger(FieldDefinitionReLoadManager.class);

    @Autowired
    private IFieldDefinitionRepository fieldDefinitionRepository;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(FieldDefinitionDO.class,this);
    }

    /**
     * 更新事件类型
     * @return
     */
    @Override
    public boolean addOrUpdate(FieldDefinitionDO fieldDefinitionDO){
        String uuid = fieldDefinitionDO.getUuid();
        logger.info("FieldDefinitionReLoadManager start, uuid:{}",uuid);
        try {
            Long timestamp = fieldDefinitionDO.getGmtModify().getTime();
            FieldDefinition oldFieldDefinition = fieldDefinitionCache.get(uuid);
            //缓存中的数据是相同版本或更新的，则不刷新
            if(oldFieldDefinition != null && oldFieldDefinition.getModifiedVersion() >= timestamp) {
                return true;
            }

            FieldDefinition newFieldDefinition = fieldDefinitionRepository.queryByUuid(uuid);
            fieldDefinitionCache.put(uuid, newFieldDefinition);
        } catch (Exception e){
            logger.error("FieldDefinitionReLoadManager failed, uuid:{}",uuid,e);
            return false;
        }
        logger.info("FieldDefinitionReLoadManager success, uuid:{}",uuid);
        return true;
    }


    /**
     * 删除事件类型
     * @param uuid
     * @return
     */
    @Override
    public boolean remove(String uuid){
        try {
            fieldDefinitionCache.remove(uuid);
        } catch (Exception e){
            return false;
        }
        return true;
    }

}
