package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.compare.CompareInfo;
import cn.tongdun.kunpeng.api.engine.model.compare.ICompareInfoRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CompareInfoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author: yuanhang
 * @date: 2020-07-27 16:39
 **/
@Repository
public class CompareInfoRepository implements ICompareInfoRepository {

    @Autowired
    CompareInfoDAO compareInfoDAO;

    @Override
    public int insertFluid(CompareInfo compareInfo) {
        return compareInfoDAO.insert(compareInfo);
    }
}
