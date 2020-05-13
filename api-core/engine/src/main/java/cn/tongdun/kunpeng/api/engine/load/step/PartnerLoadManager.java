package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.acl.engine.model.partner.PartnerDTO;
import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.acl.engine.model.partner.IPartnerRepository;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        logger.info(TraceUtils.getFormatTrace()+"PartnerLoadManager start");
        long beginTime = System.currentTimeMillis();

        List<PartnerDTO> partnerDTOList = partnerRepository.queryEnabledByPartners(partnerClusterCache.getPartners());

        if(partnerDTOList == null || partnerDTOList.isEmpty()){
            return true;
        }

        List<Partner> partnerList = partnerDTOList.stream().map(partnerDTO->{
            Partner partner = new Partner();
            BeanUtils.copyProperties(partnerDTO,partner);
            return partner;
        }).collect(Collectors.toList());

        for(Partner partner:partnerList){
            partnerCache.put(partner.getPartnerCode(),partner);
        }

        logger.info(TraceUtils.getFormatTrace()+"PartnerLoadManager success, cost:{}, size:{}",
                System.currentTimeMillis() - beginTime, partnerList.size());
        return true;
    }

}
