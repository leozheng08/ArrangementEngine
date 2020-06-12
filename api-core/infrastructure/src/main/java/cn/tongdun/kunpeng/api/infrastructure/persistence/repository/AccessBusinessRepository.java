package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.access.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.access.AccessParam;
import cn.tongdun.kunpeng.api.engine.model.access.IAccessBusinessRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AccessBusinessDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AccessParamDAO;
import cn.tongdun.kunpeng.share.dataobject.AccessBusinessDO;
import cn.tongdun.kunpeng.share.dataobject.AccessParamDO;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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
    public List<AccessBusiness> queryAllUsableAccess() {
        List<AccessBusinessDO> accessBusinesses = accessBusinessDAO.queryAllUsableAccess();
        List<AccessParamDO> accessParamDOS = accessParamDAO.selectByAccessUUIDs(accessBusinesses.stream().map(AccessBusinessDO::getUuid).collect(Collectors.toList()));
        List<AccessParam> accessParams = accessParamDOS.stream().map(r -> {
           AccessParam accessParam = new AccessParam();
           BeanUtils.copyProperties(r, accessParam);
            return accessParam;
        }).collect(Collectors.toList());
        Map<String, List<AccessParam>>  uuidAccessParamMap = accessParams.stream().collect(groupingBy(AccessParam::getAccessUuid));
        return accessBusinesses.stream().map(r -> {
            AccessBusiness accessBusiness = new AccessBusiness();
            BeanUtils.copyProperties(r, accessBusiness);
            accessBusiness.setAccessParams(uuidAccessParamMap.get(r.getUuid()));
            return accessBusiness;
        }).collect(Collectors.toList());
    }

}
