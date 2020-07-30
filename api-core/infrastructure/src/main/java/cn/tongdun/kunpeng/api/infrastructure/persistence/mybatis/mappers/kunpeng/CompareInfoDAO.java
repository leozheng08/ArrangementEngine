package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.api.engine.model.compare.CompareInfo;

/**
 * @author: yuanhang
 * @date: 2020-07-27 15:52
 **/
public interface CompareInfoDAO {


    int deleteByPrimaryKey(Long id);

    int insert(CompareInfo record);

    int insertSelective(CompareInfo record);

    CompareInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CompareInfo record);

    int updateByPrimaryKeyWithBLOBs(CompareInfo record);

    int updateByPrimaryKey(CompareInfo record);

}
