package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.compare.CompareInfo;
import cn.tongdun.kunpeng.api.engine.model.compare.ICompareInfoRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.UniqKeyEntry;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.CompareInfoDAO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public int deleteCompareFailedInfos(List<CompareInfo> compareInfos) {
        List<Long> ids = compareInfos.stream().map(CompareInfo::getId).collect(Collectors.toList());
        return compareInfoDAO.batchDeleteByPrimaryKey(ids);
    }

    @Override
    public int insert(CompareInfo compareInfo) {
        return compareInfoDAO.insert(compareInfo);
    }

    @Override
    public List<CompareInfo> selectUnComparedInfo() {
        List<CompareInfo> compareInfos = compareInfoDAO.selectUnCompareInfo();
        // 根据orderCode匹配数据
        Set<UniqKeyEntry> uniqKey = compareInfos.stream()
                .map(r -> new UniqKeyEntry(r.getPolicyName(), r.getEventOccurTime()))
                .collect(Collectors.toSet());
        List<CompareInfo> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(uniqKey)){
            uniqKey.stream().forEach(r -> {
                List<CompareInfo> compareInfos1 = compareInfoDAO.selectByPolicyNameAndEventOccurTime(r.getPolicyName(), r.getEventOccurTime());
                result.addAll(compareInfos1);
            });
        }
        return result;
    }

}
