package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.script.DynamicScript;
import cn.tongdun.kunpeng.api.engine.model.script.IDynamicScriptRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.GroovyDynamicScriptDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyScriptConfigDAO;
import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyIndicatrixItemDO;
import cn.tongdun.kunpeng.share.dataobject.PolicyScriptConfigDO;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
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
public class GroovyDynamicScriptRepository implements IDynamicScriptRepository {

    @Autowired
    GroovyDynamicScriptDAO groovyDynamicScriptDAO;

    @Autowired
    PolicyScriptConfigDAO policyScriptConfigDAO;

    @Override
    public List<DynamicScript> queryGroovyByPartners(Set<String> partners) {
        List<DynamicScriptDO> list = groovyDynamicScriptDAO.selectGroovyByPartners(partners);

        List<DynamicScript> result = list.stream().map(dynamicScriptDO -> {
            DynamicScript dynamicScript = new DynamicScript();
            BeanUtils.copyProperties(dynamicScriptDO, dynamicScript);
            return dynamicScript;
        }).collect(Collectors.toList());

        return result;
    }


    @Override
    public DynamicScript queryByUuid(String uuid) {
        DynamicScriptDO dynamicScriptDO = groovyDynamicScriptDAO.selectByUuid(uuid);
        if (dynamicScriptDO == null) {
            return null;
        }
        DynamicScript dynamicScript = new DynamicScript();
        BeanUtils.copyProperties(dynamicScriptDO, dynamicScript);
        return dynamicScript;
    }

    @Override
    public List<String> queryByPolicyUuid(String policyUuid) {
        List<PolicyScriptConfigDO> policyScriptConfigDOList = policyScriptConfigDAO.selectByPolicyUuid(policyUuid);
        if (CollectionUtils.isEmpty(policyScriptConfigDOList)) {
            return Lists.newArrayList();
        }
        return policyScriptConfigDOList.stream().map(o -> o.getDynamicScriptUuid()).collect(Collectors.toList());
    }
}
