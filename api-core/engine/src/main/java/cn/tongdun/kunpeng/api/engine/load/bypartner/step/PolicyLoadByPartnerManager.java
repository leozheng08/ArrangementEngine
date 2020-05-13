package cn.tongdun.kunpeng.api.engine.load.bypartner.step;

import cn.tongdun.kunpeng.api.engine.load.bypartner.ILoadByPartner;
import cn.tongdun.kunpeng.api.engine.load.bypartner.LoadByPartnerPipeline;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadByPartnerPipeline.NAME, phase = LoadByPartnerPipeline.LOAD_POLICY)
public class PolicyLoadByPartnerManager implements ILoadByPartner {

    private Logger logger = LoggerFactory.getLogger(PolicyLoadByPartnerManager.class);

    @Autowired
    private PolicyLoadByPartnerService loadPolicyByPartnerService;

    /**
     * 只加载一个合作方的策略
     * @param partnerCode
     * @return
     */
    @Override
    public boolean loadByPartner(String partnerCode) {
        logger.info(TraceUtils.getFormatTrace()+"LoadPolicyByPartnerManager start");

        boolean success = loadPolicyByPartnerService.loadByPartner(partnerCode);

        logger.info(TraceUtils.getFormatTrace()+"PolicyLoadManager success"+success);
        return success;
    }
}
