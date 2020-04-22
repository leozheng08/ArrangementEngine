package cn.tongdun.kunpeng.api.acl.impl.engine.model.cluster;

import cn.tongdun.kunpeng.api.acl.engine.model.cluster.IPartnerClusterRepository;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 *
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
@Repository
public class DbPartnerClusterRepository implements IPartnerClusterRepository {


    @Autowired
    private IPartnerRepository partnerRepository;


    /**
     * 没有分集群部署，则查询出所有合作方
     * @param cluster
     * @return
     */
    @Override
    public Set<String> queryPartnerByCluster(String cluster){
        Set<String> partners = partnerRepository.queryAllPartnerCode();
        return partners;
    }
}
