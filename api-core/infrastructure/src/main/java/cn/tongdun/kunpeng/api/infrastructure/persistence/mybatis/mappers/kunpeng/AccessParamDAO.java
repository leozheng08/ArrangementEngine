package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.AccessParamDO;

import java.util.List;

/**
 * @author: yuanhang
 * @date: 2020-06-12 14:20
 **/
public interface AccessParamDAO {

    /**
     * 根据access_uuid获取access_param
     * @param list
     * @return
     */
    List<AccessParamDO> selectByAccessUUIDs(List<String> list);
}
