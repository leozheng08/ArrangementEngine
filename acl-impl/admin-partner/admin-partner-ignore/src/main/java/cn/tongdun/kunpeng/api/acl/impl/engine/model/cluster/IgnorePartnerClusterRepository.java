package cn.tongdun.kunpeng.api.acl.impl.engine.model.cluster;

import cn.tongdun.kunpeng.api.acl.engine.model.cluster.IPartnerClusterRepository;
import cn.tongdun.kunpeng.api.common.Constant;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

/**
 * 只有一个default默认合作方
 * @Author: liang.chen
 * @Date: 2020/4/13 上午11:15
 */
@Repository
public class IgnorePartnerClusterRepository implements IPartnerClusterRepository{

    //只有一个默认合作方
    @Override
    public Set<String> queryPartnerByCluster(String cluster){
        return new HashSet<String>(){{add(Constant.DEFAULT_PARTNER);}};
    }


    @Override
    public Set<String> queryAllPartner(){
        return new HashSet<String>(){{add(Constant.DEFAULT_PARTNER);}};
    }
}
