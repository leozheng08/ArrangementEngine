package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.InterfaceDefinitionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InterfaceDefinitionDOMapper {

    /**
     * 查询所有在用的
     * @return
     */
    List<InterfaceDefinitionDO> selectAllAvailable();

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    InterfaceDefinitionDO selectByUuid(@Param("uuid") String uuid);

}