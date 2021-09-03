package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.DynamicScriptDAO;
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
public class DynamicScriptRepository implements IDynamicScriptRepository {

    @Autowired
    DynamicScriptDAO dynamicScriptDAO;


    @Override
    public List<DynamicScript> queryGroovyByPartners(Set<String> partners) {
        List<DynamicScriptDO> list = dynamicScriptDAO.selectGroovyByPartners(partners);

        List<DynamicScript> result = list.stream().map(dynamicScriptDO -> {
            DynamicScript dynamicScript = new DynamicScript();
            BeanUtils.copyProperties(dynamicScriptDO, dynamicScript);
            return dynamicScript;
        }).collect(Collectors.toList());

        return result;
    }


    @Override
    public DynamicScript queryByUuid(String uuid) {
        DynamicScriptDO dynamicScriptDO = dynamicScriptDAO.selectByUuid(uuid);
        if (dynamicScriptDO == null) {
            return null;
        }
        DynamicScript dynamicScript = new DynamicScript();
        BeanUtils.copyProperties(dynamicScriptDO, dynamicScript);
        return dynamicScript;
    }
}
