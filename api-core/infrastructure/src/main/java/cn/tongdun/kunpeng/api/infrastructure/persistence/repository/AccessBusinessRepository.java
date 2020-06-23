package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessParam;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.IAccessBusinessRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AccessBusinessDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AccessParamDAO;
import cn.tongdun.kunpeng.share.dataobject.AccessBusinessDO;
import cn.tongdun.kunpeng.share.dataobject.AccessParamDO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author: yuanhang
 * @date: 2020-06-12 14:02
 **/
@Repository
public class AccessBusinessRepository implements IAccessBusinessRepository {

    @Autowired
    AccessBusinessDAO accessBusinessDAO;

    @Autowired
    AccessParamDAO accessParamDAO;

    @Override
    public List<AccessBusiness> queryAllUsableAccess(Set<String> partnerCodes) {
        List<AccessBusinessDO> accessBusinesses = accessBusinessDAO.queryAllUsableAccess(partnerCodes.stream().collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(accessBusinesses)) {
            List<AccessParamDO> accessParamDOS = accessParamDAO.selectByAccessUUIDs(accessBusinesses.stream().map(AccessBusinessDO::getUuid).collect(Collectors.toList()));
            List<AccessParam> accessParams = accessParamDOS.stream().map(r -> {
                AccessParam accessParam = new AccessParam();
                BeanUtils.copyProperties(r, accessParam);
                return accessParam;
            }).collect(Collectors.toList());
            Map<String, List<AccessParam>>  uuidAccessParamMap = accessParams.stream().collect(groupingBy(AccessParam::getAccessUuid));
            List<AccessBusiness> result = accessBusinesses.stream().map(r -> {
                AccessBusiness accessBusiness = new AccessBusiness();
                BeanUtils.copyProperties(r, accessBusiness);
                accessBusiness.setAccessParams(uuidAccessParamMap.get(r.getUuid()));
                return accessBusiness;
            }).collect(Collectors.toList());
            return result;
        }
        return Lists.newArrayList();
    }

    @Override
    public AccessBusiness selectByUuid(String uuid) {
        AccessBusinessDO accessBusinessDO = accessBusinessDAO.selectByUuid(uuid);
        if (null == accessBusinessDO) {
            return null;
        }
        AccessBusiness accessBusiness = new AccessBusiness();
        BeanUtils.copyProperties(accessBusinessDO, accessBusiness);
        List<AccessParamDO> accessParams = accessParamDAO.selectByAccessUUIDs(Lists.newArrayList(accessBusiness.getUuid()));
        accessBusiness.setAccessParams(accessParams.stream().map(accessParamDO -> {
            AccessParam accessParam = new AccessParam();
            BeanUtils.copyProperties(accessParamDO, accessParam);
            return accessParam;
        }).collect(Collectors.toList()));
        return accessBusiness;
    }

}
