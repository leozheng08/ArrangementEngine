package cn.tongdun.kunpeng.api.engine.reload.impl;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplication;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplicationCache;
import cn.tongdun.kunpeng.api.engine.reload.IReload;
import cn.tongdun.kunpeng.api.engine.reload.ReloadFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: yuanhang
 * @date: 2020-07-03 14:33
 **/
@Component
public class AdminApplicationReloadManager implements IReload<AdminApplication> {

    @Autowired
    private ReloadFactory reloadFactory;

    @Autowired
    AdminApplicationCache adminApplicationCache;

    @Autowired
    IAdminApplicationRepository adminApplicationRepository;

    @PostConstruct
    public void init() {
        reloadFactory.register(AdminApplication.class, this);
    }

    @Override
    public boolean create(AdminApplication adminApplication) {
        return addOrUpdate(adminApplication.getUuid());
    }

    @Override
    public boolean update(AdminApplication adminApplication) {
        return addOrUpdate(adminApplication.getUuid());
    }

    @Override
    public boolean activate(AdminApplication adminApplication) {
        return addOrUpdate(adminApplication.getUuid());
    }

    @Override
    public boolean deactivate(AdminApplication adminApplication) {
        return addOrUpdate(adminApplication.getUuid());
    }

    @Override
    public boolean remove(AdminApplication adminApplication) {
        adminApplicationCache.remove(adminApplication);
        return true;
    }

    /**
     * application的修改新增等操作
     * @param uuid
     * @return
     */
    private boolean addOrUpdate(String uuid) {
        AdminApplicationDTO app = adminApplicationRepository.selectApplicationByUuid(uuid);
        if (null == app) {
            return true;
        }
        AdminApplication adminApplication = new AdminApplication();
        BeanUtils.copyProperties(app, adminApplication);
        adminApplicationCache.addAdminApplication(adminApplication);
        return true;
    }
}
