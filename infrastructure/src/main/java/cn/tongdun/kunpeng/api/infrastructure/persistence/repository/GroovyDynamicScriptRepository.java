package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.groovy.IGroovyDynamicScriptRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.GroovyDynamicScriptDOMapper;
import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/2/19 下午8:33
 */
@Repository
public class GroovyDynamicScriptRepository implements IGroovyDynamicScriptRepository {

    @Autowired
    GroovyDynamicScriptDOMapper dynamicScriptDOMapper;


    @Override
    public List<DynamicScript> queryGroovyByPartners(Set<String> partners){
        List<DynamicScriptDO> list = dynamicScriptDOMapper.selectGroovyByPartners(partners);

        List<DynamicScript> result = list.stream().map(dynamicScriptDO->{
            DynamicScript dynamicScript = new DynamicScript();
            BeanUtils.copyProperties(dynamicScriptDO,dynamicScript);
            return dynamicScript;
        }).collect(Collectors.toList());

        return result;
    }
}
