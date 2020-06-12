package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.engine.model.access.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import org.springframework.stereotype.Component;

/**
 * @author: yuanhang
 * @date: 2020-06-10 18:52
 **/
@Component
public class AccessBusinessReloadManager implements IReload<AccessBusiness> {

    @Override
    public boolean create(AccessBusiness accessBusiness) {

        return false;
    }

    @Override
    public boolean update(AccessBusiness accessBusiness) {
        return false;
    }

    @Override
    public boolean activate(AccessBusiness accessBusiness) {
        return false;
    }

    @Override
    public boolean deactivate(AccessBusiness accessBusiness) {
        return false;
    }

    @Override
    public boolean remove(AccessBusiness accessBusiness) {
        return false;
    }
}
