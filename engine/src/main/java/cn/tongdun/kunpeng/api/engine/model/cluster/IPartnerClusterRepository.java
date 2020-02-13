package cn.tongdun.kunpeng.api.engine.model.cluster;

import cn.tongdun.kunpeng.api.engine.model.eventtype.EventType;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IPartnerClusterRepository {

    Set<String> queryPartnerByCluster(String cluster);

    Set<String> queryAllPartner();
}
