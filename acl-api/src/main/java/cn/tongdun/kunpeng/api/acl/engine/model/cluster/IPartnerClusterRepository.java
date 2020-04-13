package cn.tongdun.kunpeng.api.acl.engine.model.cluster;

import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPartnerClusterRepository {

    Set<String> queryPartnerByCluster(String cluster);

    Set<String> queryAllPartner();
}
