package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;

import cn.tongdun.kunpeng.share.dataobject.PartnerClusterDO;

import java.util.List;

public interface PartnerClusterDAO {

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


    /**
     * 根据uuid查询数据
     * @param uuid
     * @return
     */
    PartnerClusterDO selectByUuid(String uuid);

    int downCluster(String uuid);

    int upCluster(String uuid);


    /**
     * 根据合作方名称查询合作方信息
     * @param partnerCode
     * @return
     */
    PartnerClusterDO selectByPartnerCode(String partnerCode);
}