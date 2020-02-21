package cn.tongdun.kunpeng.api.engine.load.bypartner.step;

import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.load.bypartner.ILoadByPartner;
import cn.tongdun.kunpeng.api.engine.load.bypartner.LoadByPartnerPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2019/12/12 上午10:43
 */

@Component
@Step(pipeline = LoadByPartnerPipeline.NAME, phase = LoadPipeline.LOAD_PARTNER)
public class PartnerLoadByPartnerManager implements ILoadByPartner {

    private Logger logger = LoggerFactory.getLogger(PartnerLoadByPartnerManager.class);

    @Autowired
    PartnerCache partnerCache;

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    IPartnerRepository partnerRepository;

    @Override
    public boolean loadByPartner(String partnerCode){
        logger.info("PartnerLoadByPartnerManager start");

        Partner partner = partnerRepository.queryByPartnerCode(partnerCode);
        if(partner == null){
            return false;
        }

        partnerCache.put(partner.getPartnerCode(),partner);
        logger.info("PartnerLoadByPartnerManager success");
        return true;
    }
}
