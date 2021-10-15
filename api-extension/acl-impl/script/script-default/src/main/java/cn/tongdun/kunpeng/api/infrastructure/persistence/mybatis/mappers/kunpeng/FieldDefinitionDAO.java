package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.FieldDefinitionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FieldDefinitionDAO {

    List<FieldDefinitionDO> selectAll();

    /**
     * 查询所有字段
     */
    List<FieldDefinitionDO> selectByFieldType(String fieldType);

    /**
     * 根据uuid查询
     */
    FieldDefinitionDO selectByUuid(@Param("uuid") String uuid);

}