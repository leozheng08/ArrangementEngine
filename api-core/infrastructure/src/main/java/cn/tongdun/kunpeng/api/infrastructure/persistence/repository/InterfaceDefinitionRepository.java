package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.intfdefinition.IInterfaceDefinitionRepository;
import cn.tongdun.kunpeng.api.engine.model.intfdefinition.InterfaceDefinition;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.InterfaceDefinitionDAO;
import cn.tongdun.kunpeng.share.dataobject.InterfaceDefinitionDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/2/16 上午3:48
 */
@Repository
public class InterfaceDefinitionRepository implements IInterfaceDefinitionRepository{

    @Autowired
    private InterfaceDefinitionDAO interfaceDefinitionDAO;

    @Override
    public List<InterfaceDefinition> queryAllAvailable(){
        List<InterfaceDefinitionDO> interfaceDefinitionDOList = interfaceDefinitionDAO.selectAllAvailable();

        if(interfaceDefinitionDOList == null) {
            return null;
        }

        List<InterfaceDefinition> result = null;
        result = interfaceDefinitionDOList.stream().map(interfaceDefinitionDO->{
            InterfaceDefinition interfaceDefinition = new InterfaceDefinition();
            BeanUtils.copyProperties(interfaceDefinitionDO,interfaceDefinition);
            return interfaceDefinition;
        }).collect(Collectors.toList());

        return result;
    }

    @Override
    public InterfaceDefinition queryByUuid(String uuid){
        InterfaceDefinitionDO interfaceDefinitionDO = interfaceDefinitionDAO.selectByUuid(uuid);
        if(interfaceDefinitionDO == null){
            return null;
        }
        InterfaceDefinition interfaceDefinition  = new InterfaceDefinition();
        BeanUtils.copyProperties(interfaceDefinitionDO,interfaceDefinition);
        return interfaceDefinition;
    }
}
