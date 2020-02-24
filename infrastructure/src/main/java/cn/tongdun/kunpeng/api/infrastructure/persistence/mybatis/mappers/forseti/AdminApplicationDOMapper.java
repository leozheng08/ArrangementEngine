package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.forseti;

import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.AdminApplicationDO;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2020/2/21 下午5:23
 */
public interface AdminApplicationDOMapper {

    public AdminApplicationDO queryByUuid(String uuid);

    public List<AdminApplicationDO> queryApplicationsByPartners(Set<String> partners);
}
