package cn.tongdun.kunpeng.api.acl.impl.admin.partner.ignore;

import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
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
public class IgnorePartnerLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(IgnorePartnerLoadManager.class);

    @Autowired
    PartnerCache partnerCache;

    @Autowired
    PartnerClusterCache partnerClusterCache;


    @Override
    public boolean load(){
        //加载默认合作方
        Partner defaultPartner = createDefaultPartner();
        partnerCache.put(defaultPartner.getPartnerCode(),defaultPartner);
        return true;
    }

    private Partner createDefaultPartner(){
        Partner partner = new Partner();
        partner.setPartnerCode(Constant.DEFAULT_PARTNER);
        partner.setUuid(Constant.DEFAULT_PARTNER);
        partner.setDisplayName(Constant.DEFAULT_PARTNER);
        partner.setStatus(1);
        return partner;
    }
}
