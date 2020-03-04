package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.model.cluster.IPartnerClusterRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.AdminPartnerDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PartnerClusterDOMapper;
import cn.tongdun.kunpeng.share.dataobject.PartnerClusterDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class PartnerClusterRepository implements IPartnerClusterRepository {


    @Autowired
    private PartnerClusterDOMapper partnerClusterDOMapper;

    @Autowired
    private AdminPartnerDOMapper adminPartnerDOMapper;

    /**
     * 根据集群名取得集群下的合作方列表
     * @param cluster
     * @return
     */
    @Override
    public Set<String> queryPartnerByCluster(String cluster){
        List<PartnerClusterDO> list = partnerClusterDOMapper.selectAvailableByCluster(cluster);
        Set partners = new HashSet();
        for(PartnerClusterDO partnerClusterDO:list){
            partners.add(partnerClusterDO.getPartnerCode());
        }
        return partners;
    }

    /**
     * 查询所有合作方
     * @return
     */
    @Override
    public Set<String> queryAllPartner(){
        List<String> partnerCodes = adminPartnerDOMapper.selectAllEnabledPartnerCodes();
        Set partners = new HashSet(partnerCodes);
        return partners;

    }
}
