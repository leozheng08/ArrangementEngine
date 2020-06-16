package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.access.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: yuanhang
 * @date: 2020-06-10 18:52
 **/
@Component
public class AccessBusinessReloadManager implements IReload<AccessBusiness> {

    @Autowired
    AccessBusinessCache accessBusinessCache;

    @Override
    public boolean create(AccessBusiness accessBusiness) {
        accessBusinessCache.put(accessBusiness.getAppName(), accessBusiness);
        return true;
    }

    @Override
    public boolean update(AccessBusiness accessBusiness) {
        accessBusinessCache.put(accessBusiness.getAppName(), accessBusiness);
        return true;
    }

    @Override
    public boolean activate(AccessBusiness accessBusiness) {
        accessBusinessCache.put(accessBusiness.getAppName(), accessBusiness);
        return true;
    }

    @Override
    public boolean deactivate(AccessBusiness accessBusiness) {
        accessBusinessCache.put(accessBusiness.getAppName(), accessBusiness);
        return true;
    }

    @Override
    public boolean remove(AccessBusiness accessBusiness) {
        accessBusinessCache.remove(accessBusiness.getAppName());
        return true;
    }
}
