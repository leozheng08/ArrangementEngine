package cn.tongdun.kunpeng.api.infrastructure.persistence.docache;

import cn.tongdun.kunpeng.api.engine.model.constant.CommonStatusEnum;
import cn.tongdun.kunpeng.api.engine.reload.docache.AbstractDataObjectCache;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.FieldDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.DecisionFlowDO;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import cn.tongdun.kunpeng.share.dataobject.FieldDefinitionDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2020/4/26 下午2:34
 */
@Component
public class FieldDefinitionDOCache extends AbstractDataObjectCache<FieldDefinitionDO> {

    @Autowired
    private FieldDefinitionDAO fieldDefinitionDAO;


    @Override
    public void refresh(String uuid) {
        FieldDefinitionDO fieldDefinitionDO = fieldDefinitionDAO.selectByUuid(uuid);
        set(fieldDefinitionDO);
    }

    @Override
    public void refreshAll() {
        List<FieldDefinitionDO> list = fieldDefinitionDAO.selectAll();
        if(list == null || list.isEmpty()){
            return;
        }
        list.forEach(dataObject ->{
            set(dataObject);
        });
    }

    /**
     * 添加索引
     * @param dataObject
     */
    @Override
    public void setByIdx(FieldDefinitionDO dataObject){
        //添加合作方索引. 索引按zadd(idex_name,分数固定为0,索引值:uuid)方式添加，通过zrangebylex来按索引查询
        scoreKVRepository.zadd(cacheKey+"_fieldType",0,dataObject.getFieldType()+":"+dataObject.getUuid());
    }

    /**
     * 删除索引
     * @param dataObject
     */
    @Override
    public void removeIdx(FieldDefinitionDO dataObject){
        scoreKVRepository.zrem(cacheKey+"_fieldType",dataObject.getFieldType()+":"+dataObject.getUuid());
    }


    @Override
    public boolean isValid(FieldDefinitionDO dataObject){
        if(dataObject.getStatus() != null && dataObject.getStatus().equals(CommonStatusEnum.CLOSE.getCode())) {
            return false;
        }
        if(dataObject.isDeleted()) {
            return false;
        }

        return true;
    }
}
