package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

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

    List<EventTypeDO> selectAll();

    /**
     * 根据uuid查询
     * @param uuid
     * @return
     */
    EventTypeDO selectByUuid(@Param("uuid") String uuid);

}