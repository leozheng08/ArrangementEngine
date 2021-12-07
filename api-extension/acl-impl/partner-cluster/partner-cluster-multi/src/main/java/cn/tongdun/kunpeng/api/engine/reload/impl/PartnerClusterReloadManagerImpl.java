package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.api.engine.reload.IPartnerClusterReloadManager;
import cn.tongdun.kunpeng.api.service.ILoadPartnerDataService;
import cn.tongdun.kunpeng.api.service.IRemovePartnerDataService;
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
    ILocalEnvironment localEnvironment;

    @Override
    public Boolean reload(String partnerCode,String cluster,Integer isCreate) {
        logger.info("Received a message partnerCode={},cluster={},isCreate={}",partnerCode,cluster,isCreate);
        if(isCreate == 1){
            return load(partnerCode,cluster);
        }else {
            return remove(partnerCode);
        }
    }

    /**
     * load缓存
     * @param
     * @return
     */
    public boolean load(String partnerCode,String cluster){
        try{
            //校验集群
            if(!localEnvironment.getCluster().equals(cluster)){
                return false;
            }
            loadPartnerDataService.load(partnerCode);
            return true;
        }catch (Exception e){
            logger.error("load partner cluster cache fail!e={}", ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    /**
     * remove缓存
     * @return
     */
    public boolean remove(String partnerCode){
        try{
            removePartnerDataService.remove(partnerCode);
            return true;
        }catch (Exception e){
            logger.error("remove partner cluster cache fail!e={}", ExceptionUtils.getStackTrace(e));
        }
        return false;
    }
}
