package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;

import java.util.List;

public interface CustomListValueDOMapper {


    /**
     * 根据自定义列表查询数量
     * @param customListUuid
     * @return
     */
    int selectCountByListUuid(String customListUuid);

    /**
     * 分页加载自定义列表
     * @param customListUuid
     * @return
     */
    List<CustomListValueDO> selectByListUuid(String customListUuid,Integer offset,Integer size);


}