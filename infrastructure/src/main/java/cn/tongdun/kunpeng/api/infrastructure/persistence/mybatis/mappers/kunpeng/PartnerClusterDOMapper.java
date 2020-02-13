package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PartnerClusterDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PartnerClusterDOMapper {

    /**
     * 根据集群名查询合作方列表
     *
     * @param cluster
     * @return
     */
    List<PartnerClusterDO> selectAvailableByCluster(String cluster);

    /**
     * 查询所有可用集群合作方列表
     * @return
     */
    List<PartnerClusterDO> selectAllAvailable();


}