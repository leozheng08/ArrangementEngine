package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.CustomListDO;

import java.util.List;

public interface CustomListDAO {


    /**
     * 查询所有在用的自定义列表
     * @return
     */
    List<CustomListDO> selectAllAvailable();


}