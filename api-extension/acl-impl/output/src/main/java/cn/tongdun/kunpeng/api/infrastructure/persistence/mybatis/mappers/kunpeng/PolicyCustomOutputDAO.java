package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyCustomOutputDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/12 21:08
 */
@Mapper
public interface PolicyCustomOutputDAO {

    PolicyCustomOutputDO queryByUuid(String uuid);

    List<PolicyCustomOutputDO> queryByPolicyDefinitionUuid(String uuid);

    List<PolicyCustomOutputDO> queryByPartner(Set<String> partner);

    List<PolicyCustomOutputDO> queryAll();
}
