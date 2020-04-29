package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.reload.docache.Cacheable;
import cn.tongdun.kunpeng.share.dataobject.EventTypeDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface EventTypeDAO {

    /**
     * 查询可用的类型
     *
     * @param codeList
     * @return
     */
    List<EventTypeDO> selectAvailableByCodes(@Param("list") Set<String> codeList);

    List<EventTypeDO> selectAllAvailable();

    @Cacheable(idxName = "all")
    List<EventTypeDO> selectAll();

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    @Cacheable
    EventTypeDO selectByUuid(@Param("uuid") String uuid);

}