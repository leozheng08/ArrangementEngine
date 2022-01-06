package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PolicyCustomOutputElementDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/18 10:24
 */
@Mapper
public interface PolicyCustomOutputElementDAO {
    List<PolicyCustomOutputElementDO> queryByCustomOutputUuid(String customOutputUuid);
}
