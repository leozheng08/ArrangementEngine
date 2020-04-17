package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.FieldDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.FieldDefinitionDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class FieldRepository implements IFieldDefinitionRepository {


    @Autowired
    private FieldDefinitionDAO fieldDefinitionDAO;


    @Override
    public List<FieldDefinition> queryAllSystemField(){

        List<FieldDefinitionDO> list = fieldDefinitionDAO.selectByFieldType("sys");

        List<FieldDefinition> result = null;
        if(list != null){
            result = list.stream().map(fieldDefinitionDO->{
                FieldDefinition fieldDefinition = new FieldDefinition();
                BeanUtils.copyProperties(fieldDefinitionDO,fieldDefinition);
                return fieldDefinition;
            }).collect(Collectors.toList());
        }
        return result;

    }


    @Override
    public List<FieldDefinition> queryAllExtendField(){

        List<FieldDefinitionDO> list = fieldDefinitionDAO.selectByFieldType("ext");

        List<FieldDefinition> result = null;
        if(list != null){
            result = list.stream().map(fieldDefinitionDO->{
                FieldDefinition fieldDefinition = new FieldDefinition();
                BeanUtils.copyProperties(fieldDefinitionDO,fieldDefinition);
                return fieldDefinition;
            }).collect(Collectors.toList());
        }
        return result;

    }

    @Override
    public FieldDefinition queryByUuid(String uuid){
        FieldDefinitionDO fieldDefinitionDO = fieldDefinitionDAO.selectByUuid(uuid);
        if(fieldDefinitionDO == null){
            return null;
        }
        FieldDefinition fieldDefinition = new FieldDefinition();
        BeanUtils.copyProperties(fieldDefinitionDO,fieldDefinition);
        return fieldDefinition;
    }
}
