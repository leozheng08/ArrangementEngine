package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.model.rule.function.namelist.CustomListValue;
import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomListValueDOMapper {


    /**
     * 根据自定义列表查询数量
     * @param customListUuid
     * @return
     */
    int selectCountByListUuid(@Param("customListUuid") String customListUuid);

    /**
     * 分页加载自定义列表
     * @param customListUuid
     * @return
     */
    List<CustomListValueDO> selectByListUuid(@Param("customListUuid") String customListUuid, @Param("offset") Integer offset, @Param("size") Integer size);


    /**
     * 根据customListValueUuid 查询列表数据
     * @param uuid
     * @return
     */
    CustomListValueDO selectByUuid(String uuid);

    /**
     * 根据customListValueUuid 查询列表数据
     * @param uuids
     * @return
     */
    List<CustomListValueDO> selectByUuids(List<String> uuids);

}