package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.AccessBusinessDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author: yuanhang
 * @date: 2020-06-12 11:29
 **/
public interface AccessBusinessDAO {

    /**
     * 查询所有未删除的接入业务
     * @return
     */
    List<AccessBusinessDO> queryAllUsableAccess(@Param("list") List<String> list);

    /**
     * 根据uuid查询AccessBusiness
     * @param uuid
     * @return
     */
    @Cacheable
    AccessBusinessDO selectByUuid(String uuid);

}
