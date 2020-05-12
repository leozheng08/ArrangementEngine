package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.FieldDefinitionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FieldDefinitionDAO {

    //todo 后期优化，按分页查询所有
    List<FieldDefinitionDO> selectAll();

    /**
     * 查询所有字段
     * @return
     */
    @Cacheable(idxName = "fieldType")
    List<FieldDefinitionDO> selectByFieldType(String fieldType);

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    @Cacheable
    FieldDefinitionDO selectByUuid(@Param("uuid") String uuid);

}