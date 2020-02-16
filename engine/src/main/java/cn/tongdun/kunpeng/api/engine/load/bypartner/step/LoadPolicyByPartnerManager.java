package cn.tongdun.kunpeng.api.engine.load.bypartner.step;

import cn.tongdun.kunpeng.api.engine.cache.LocalCacheService;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyModifiedDTO;
import cn.tongdun.kunpeng.api.engine.load.bypartner.ILoadByPartner;
import cn.tongdun.kunpeng.api.engine.load.bypartner.LoadByPartnerPipeline;
import cn.tongdun.kunpeng.api.engine.load.step.LoadPolicyTask;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.tdframework.core.concurrent.ThreadService;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadByPartnerPipeline.NAME, phase = LoadByPartnerPipeline.LOAD_POLICY)
public class LoadPolicyByPartnerManager implements ILoadByPartner {

    private Logger logger = LoggerFactory.getLogger(LoadPolicyByPartnerManager.class);

    @Autowired
    private LoadPolicyByPartnerService loadPolicyByPartnerService;

    /**
     * 只加载一个合作方的策略
     * @param partnerCode
     * @return
     */
    @Override
    public boolean loadByPartner(String partnerCode) {
        logger.info("LoadPolicyByPartnerManager start");

        boolean success = loadPolicyByPartnerService.loadByPartner(partnerCode);

        logger.info("PolicyLoadManager success"+success);
        return success;
    }
}
