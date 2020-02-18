package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.IPartnerClusterRepository;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.common.config.ILocalEnvironment;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/12 上午10:43
 */

@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_PARTNER)
public class PartnerLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(PartnerLoadManager.class);

    @Autowired
    PartnerCache partnerCache;

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    IPartnerRepository partnerRepository;

    @Override
    public boolean load(){
        logger.info("PartnerLoadManager start");

        List<Partner> partnerList = partnerRepository.queryEnabledByPartners(partnerClusterCache.getPartners());

        for(Partner partner:partnerList){
            partnerCache.put(partner.getPartnerCode(),partner);
        }

        logger.info("PartnerLoadManager success, size:"+partnerList.size());
        return true;
    }
}
