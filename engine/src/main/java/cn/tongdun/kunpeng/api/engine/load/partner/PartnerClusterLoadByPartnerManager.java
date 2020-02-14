package cn.tongdun.kunpeng.api.engine.load.partner;

import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
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
@Step(pipeline = LoadByPartnerPipeline.NAME, phase = LoadByPartnerPipeline.LOAD_PARTNER)
public class PartnerClusterLoadByPartnerManager implements ILoadByPartner {

    private Logger logger = LoggerFactory.getLogger(PartnerClusterLoadByPartnerManager.class);


    @Autowired
    PartnerClusterCache partnerClusterCache;


    /**
     * 加载单个合作方，供集群迁移时调用
     * @param partnerCode
     * @return
     */
    @Override
    public boolean loadByPartner(String partnerCode){
        partnerClusterCache.addPartner(partnerCode);
        return true;
    }
}
