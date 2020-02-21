package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.adminapplication.AdminApplication;
import cn.tongdun.kunpeng.api.engine.model.adminapplication.AdminApplicationCache;
import cn.tongdun.kunpeng.api.engine.model.adminapplication.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/12 上午10:43
 */

@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_PARTNER)
public class AdminApplicationLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(AdminApplicationLoadManager.class);

    @Autowired
    AdminApplicationCache adminApplicationCache;

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    IAdminApplicationRepository adminApplicationRepository;

    @Override
    public boolean load(){
        logger.info("PartnerLoadManager start");

        List<AdminApplication> adminApplicationList = adminApplicationRepository.queryApplicationsByPartners(partnerClusterCache.getPartners());

        for(AdminApplication adminApplication:adminApplicationList){
            adminApplicationCache.addAdminApplication(adminApplication);
        }

        logger.info("PartnerLoadManager success, size:"+adminApplicationList.size());
        return true;
    }
}
