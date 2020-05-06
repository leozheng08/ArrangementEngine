package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.InterfaceDefinitionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterfaceDefinitionDAO {

    /**
     * 查询所有在用的
     * @return
     */
    @Cacheable(idxName = "allAvailable" )
    List<InterfaceDefinitionDO> selectAllAvailable();

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    @Cacheable
    InterfaceDefinitionDO selectByUuid(@Param("uuid") String uuid);

}