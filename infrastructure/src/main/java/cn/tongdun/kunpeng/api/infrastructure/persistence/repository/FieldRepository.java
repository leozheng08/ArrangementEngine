package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.field.IFieldDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.FieldDefinitionDOMapper;
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
    private FieldDefinitionDOMapper fieldDefinitionDOMapper;


    @Override
    public List<FieldDefinition> queryAllSystemField(){

        List<FieldDefinitionDO> list = fieldDefinitionDOMapper.selectByFieldType("sys");

        List<FieldDefinition> result = null;
        if(list != null){
            result = list.stream().map(ruleFieldDO->{
                FieldDefinition ruleField = new FieldDefinition();
                BeanUtils.copyProperties(ruleFieldDO,ruleField);
                return ruleField;
            }).collect(Collectors.toList());
        }
        return result;

    }


    @Override
    public List<FieldDefinition> queryAllExtendField(){

        List<FieldDefinitionDO> list = fieldDefinitionDOMapper.selectByFieldType("ext");

        List<FieldDefinition> result = null;
        if(list != null){
            result = list.stream().map(ruleFieldDO->{
                FieldDefinition ruleField = new FieldDefinition();
                BeanUtils.copyProperties(ruleFieldDO,ruleField);
                return ruleField;
            }).collect(Collectors.toList());
        }
        return result;

    }
}
