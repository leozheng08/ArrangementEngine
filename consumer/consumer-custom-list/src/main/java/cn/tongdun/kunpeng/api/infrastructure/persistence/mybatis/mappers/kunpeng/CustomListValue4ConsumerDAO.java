package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;

import java.util.List;
import java.util.Map;

public interface CustomListValue4ConsumerDAO {
    /**
     *
     * @mbggenerated
     */
    int insert(CustomListValueDO record);

    /**
     * 根据条件查询
     * @param map
     * @return
     */
    List<CustomListValueDO> queryByParams(Map<String, Object> map);

    /**
     * 更新
     * @param customListValueDO
     * @return
     */
    Integer update(CustomListValueDO customListValueDO);
}