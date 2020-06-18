package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.access.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.model.access.IAccessBusinessRepository;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: yuanhang
 * @date: 2020-06-10 18:52
 **/
@Component
public class AccessBusinessReloadManager implements IReload<AccessBusiness> {
    private Logger logger = LoggerFactory.getLogger(AccessBusinessReloadManager.class);

    @Autowired
    AccessBusinessCache accessBusinessCache;

    @Autowired
    IAccessBusinessRepository accessBusinessRepository;

    @Autowired
    private ReloadFactory reloadFactory;

    @PostConstruct
    public void init(){
        reloadFactory.register(AccessBusiness.class,this);
    }

    @Override
    public boolean create(AccessBusiness accessBusiness) {
        return addOrUpdate(accessBusiness);
    }

    @Override
    public boolean update(AccessBusiness accessBusiness) {
        return addOrUpdate(accessBusiness);
    }

    @Override
    public boolean activate(AccessBusiness accessBusiness) {
        return addOrUpdate(accessBusiness);
    }

    @Override
    public boolean deactivate(AccessBusiness accessBusiness) {
        return addOrUpdate(accessBusiness);
    }

    @Override
    public boolean remove(AccessBusiness accessBusiness) {
        return true;
    }

    private boolean addOrUpdate(AccessBusiness accessBusiness) {
        String uuid = accessBusiness.getUuid();
        logger.debug(TraceUtils.getFormatTrace() + "start reload access business :{}", accessBusiness.getUuid());
        AccessBusiness access = accessBusinessRepository.selectByUuid(uuid);
        if (null == access) {
            return true;
        }
        if (access.getStatus().equals(0) || access.getIsDeleted().equals(1)) {
            accessBusinessCache.remove(access.getAppName());
            return true;
        }
        accessBusinessCache.put(access.getAppName(), access);
        logger.debug(TraceUtils.getFormatTrace()+"reload access business success, uuid:{}",uuid);
        return true;
    }

}
