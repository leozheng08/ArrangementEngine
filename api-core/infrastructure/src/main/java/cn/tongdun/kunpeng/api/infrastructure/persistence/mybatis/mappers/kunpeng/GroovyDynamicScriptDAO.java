package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.DynamicScriptDO;

import java.util.List;
import java.util.Set;

public interface GroovyDynamicScriptDAO {

    //todo 后期优化，按分页查询所有
    List<DynamicScriptDO> selectAll();

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