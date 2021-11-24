package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.PartnerClusterEventDO;
import cn.tongdun.kunpeng.api.service.ILoadPartnerDataService;
import cn.tongdun.kunpeng.api.service.IRemovePartnerDataService;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PartnerClusterDAO;
import cn.tongdun.kunpeng.share.dataobject.PartnerClusterDO;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: mengtao
 * @create: 2021-11-04 09:55
 */

@Component
public class PartnerClusterReloadManager implements IReload<PartnerClusterEventDO> {

    private Logger logger = LoggerFactory.getLogger(PartnerClusterReloadManager.class);

    @Autowired
    private PartnerClusterDAO partnerClusterDAO;

    @Autowired
    ILocalEnvironment localEnvironment;

    @Autowired
    private ILoadPartnerDataService loadPartnerDataServiceImpl;

    @Autowired
    private IRemovePartnerDataService removePartnerDataServiceImpl;

    @Autowired
    private ReloadFactory reloadFactory;


    @PostConstruct
    public void init() {
        reloadFactory.register(PartnerClusterEventDO.class, this);
    }

    @Override
    public boolean activate(PartnerClusterEventDO eventDO) {
        try {
            PartnerClusterDO partnerClusterDO = partnerClusterDAO.selectByUuid(eventDO.getUuid());
            //加载到默认配置的集群
            if (partnerClusterDO.getCluster().equals(localEnvironment.getCluster())) {
                loadPartnerDataServiceImpl.load(partnerClusterDO.getPartnerCode());
                //加载完成
                partnerClusterDO.setIsNew(0);
                partnerClusterDAO.update(partnerClusterDO);
            }
            return true;
        }catch (Exception e){
            logger.error(TraceUtils.getFormatTrace() + "activate partner data failed, uuid:{}", eventDO.getUuid() ,e);
            return false;
        }
    }

    @Override
    public boolean deactivate(PartnerClusterEventDO eventDO) {
        try {
            PartnerClusterDO partnerClusterDO = partnerClusterDAO.selectByUuid(eventDO.getUuid());
            //删除缓存
            if (partnerClusterDO.getCluster().equals(localEnvironment.getCluster())) {
                removePartnerDataServiceImpl.remove(partnerClusterDO.getPartnerCode());
                //设置不可用
                partnerClusterDO.setIsNew(1);
                partnerClusterDAO.update(partnerClusterDO);
            }
            return true;
        }catch (Exception e){
            logger.error(TraceUtils.getFormatTrace() + "deactivate partner data failed, uuid:{}", eventDO.getUuid() ,e);
            return false;
        }
    }

    @Override
    public boolean remove(PartnerClusterEventDO partnerClusterEventDO) {
        return false;
    }

    @Override
    public boolean create(PartnerClusterEventDO eventDO) {
        try {
            PartnerClusterDO partnerClusterDO = partnerClusterDAO.selectByUuid(eventDO.getUuid());
            if(!localEnvironment.getCluster().equals(partnerClusterDO.getCluster())){
                return false;
            }
            //加载到默认配置的集群
            if (partnerClusterDO.getCluster().equals(localEnvironment.getCluster())) {
                loadPartnerDataServiceImpl.load(partnerClusterDO.getPartnerCode());
                //加载完成
                partnerClusterDO.setIsNew(0);
                partnerClusterDAO.update(partnerClusterDO);
            }
            return true;
        }catch (Exception e){
            logger.error(TraceUtils.getFormatTrace() + "load partner data failed, uuid:{}", eventDO.getUuid() ,e);
            return false;
        }
    }

    @Override
    public boolean update(PartnerClusterEventDO eventDO) {
        try {
            PartnerClusterDO partnerClusterDO = partnerClusterDAO.selectByUuid(eventDO.getUuid());
            if (partnerClusterDO.getIsNew() != 1) {
                return true;
            }
            //先下掉
            removePartnerDataServiceImpl.remove(partnerClusterDO.getPartnerCode());
            //上线
            //校验是否在该集群加载
            if (!partnerClusterDO.getCluster().equals(localEnvironment.getCluster())) {
                return false;
            }
            loadPartnerDataServiceImpl.load(partnerClusterDO.getPartnerCode());
            //加载完成
            partnerClusterDO.setIsNew(0);
            int update = partnerClusterDAO.update(partnerClusterDO);
            if(update != 1){
                logger.error(TraceUtils.getFormatTrace() + "update partner data failed, uuid:{}", eventDO.getUuid());
            }
            return true;
        }catch (Exception e){
            logger.error(TraceUtils.getFormatTrace() + "update partner data failed, uuid:{}", eventDO.getUuid());
            return false;
        }
    }


}
