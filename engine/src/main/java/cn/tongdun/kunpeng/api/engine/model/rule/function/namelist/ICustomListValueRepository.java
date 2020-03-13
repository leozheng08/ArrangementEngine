package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;

import cn.tongdun.kunpeng.share.dataobject.CustomListValueDO;

import java.util.List;

/**
 * 自定义列表数据从数据库查询
 * @Author: liang.chen
 * @Date: 2020/2/28 下午3:08
 */
public interface ICustomListValueRepository {

    /**
     * 查询所有在用的自定义列表
     * @return
     */
    List<String> selectAllAvailable();


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
    List<CustomListValue> selectByListUuid(String customListUuid, Integer offset, Integer size);

    /**
     * 根据customListValueUuid 查询列表数据
     * @param uuid
     * @return
     */
    CustomListValue selectByUuid(String uuid);

    /**
     * 根据多个customListValueUuid 查询列表数据
     * @param uuids
     * @return
     */
    List<CustomListValue> selectByUuids(List<String> uuids);

}
