package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.api.engine.reload.IPartnerClusterReloadManager;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PartnerClusterDAO;
import cn.tongdun.kunpeng.api.service.ILoadPartnerDataService;
import cn.tongdun.kunpeng.api.service.IRemovePartnerDataService;
import cn.tongdun.kunpeng.share.dataobject.PartnerClusterDO;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: mengtao
 * @create: 2021-12-01 15:38
 */

@Component
public class PartnerClusterReloadManagerImpl implements IPartnerClusterReloadManager {

    private Logger logger = LoggerFactory.getLogger(PartnerClusterReloadManagerImpl.class);

    @Autowired
    private ILoadPartnerDataService loadPartnerDataService;

    @Autowired
    private IRemovePartnerDataService removePartnerDataService;

    @Autowired
    private PartnerClusterDAO partnerClusterDAO;

    @Autowired
    ILocalEnvironment localEnvironment;

    @Override
    public Boolean reload(String partnerCode,Integer isCreate) {
        logger.info("Received a message partnerCode={},isCreate={}",partnerCode,isCreate);
        PartnerClusterDO partnerClusterDO = partnerClusterDAO.selectByPartnerCode(partnerCode);
        if(null == partnerClusterDO){
            return false;
        }
        if(isCreate == 1){
            return load(partnerClusterDO);
        }else {
            return remove(partnerClusterDO);
        }
    }

    public boolean load(PartnerClusterDO clusterDO){
        try{
            //校验集群
            if(!localEnvironment.getCluster().equals(clusterDO.getCluster())){
                return false;
            }
            loadPartnerDataService.load(clusterDO.getPartnerCode());
            return true;
        }catch (Exception e){
            logger.error("load partner cluster cache fail!e={}", ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    public boolean remove(PartnerClusterDO clusterDO){
        try{
            removePartnerDataService.remove(clusterDO.getPartnerCode());
            return true;
        }catch (Exception e){
            logger.error("remove partner cluster cache fail!e={}", ExceptionUtils.getStackTrace(e));
        }
        return false;
    }
}
