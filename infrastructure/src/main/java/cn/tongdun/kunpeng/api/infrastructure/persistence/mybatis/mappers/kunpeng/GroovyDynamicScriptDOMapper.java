package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;

import java.util.List;
import java.util.Set;

public interface GroovyDynamicScriptDOMapper {



    /**
     * 根据合作方查询
     * @param partners
     * @return
     */
    List<DynamicScriptDO> selectGroovyByPartners(Set partners);

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    DynamicScriptDO selectByUuid(String uuid);
}