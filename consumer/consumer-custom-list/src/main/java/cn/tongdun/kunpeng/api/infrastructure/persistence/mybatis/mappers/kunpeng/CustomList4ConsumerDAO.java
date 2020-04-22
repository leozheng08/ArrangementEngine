package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.CustomListDO;
import org.apache.ibatis.annotations.Param;

public interface CustomList4ConsumerDAO {

    /**
     * 根据uuid查询
     *
     * @param uuid
     * @return
     */
    CustomListDO selectByUuid(@Param("uuid") String uuid);
}