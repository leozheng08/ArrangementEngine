package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;


import cn.tongdun.kunpeng.share.dataobject.AdminApplicationDO;

import java.util.List;
import java.util.Set;

public interface AdminApplicationDAO {

    public AdminApplicationDO queryByUuid(String uuid);

    public List<AdminApplicationDO> queryApplicationsByPartners(Set<String> partners);

}
