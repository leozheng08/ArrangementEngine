package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.acl.engine.model.application.AdminApplicationDTO;
import cn.tongdun.kunpeng.api.acl.engine.model.application.IAdminApplicationRepository;
import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplication;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplicationCache;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        //定期刷新缓存，防止新接入应用无法触发guava刷新
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            load();
            logger.info("AdminApplicationLoadManager定时刷新缓存成功");
        }, 0, 10, TimeUnit.MINUTES);
    }

    @Override
    public boolean load() {
        logger.info(TraceUtils.getFormatTrace() + "AdminApplicationLoadManager start");
        long beginTime = System.currentTimeMillis();

        List<AdminApplicationDTO> adminApplicationDTOList = adminApplicationRepository.queryApplicationsByPartners(partnerClusterCache.getPartners());

        if (adminApplicationDTOList == null || adminApplicationDTOList.isEmpty()) {
            return true;
        }

        List<AdminApplication> adminApplicationList = adminApplicationDTOList.stream().map(adminApplicationDTO -> {
            AdminApplication adminApplication = new AdminApplication();
            BeanUtils.copyProperties(adminApplicationDTO, adminApplication);
            return adminApplication;
        }).collect(Collectors.toList());

        for (AdminApplication adminApplication : adminApplicationList) {
            adminApplicationCache.addAdminApplication(adminApplication);
        }

        logger.info(TraceUtils.getFormatTrace() + "AdminApplicationLoadManager success, cost:{}, size:{}",
                System.currentTimeMillis() - beginTime, adminApplicationList.size());
        return true;
    }

}
