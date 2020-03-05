package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.IPartnerClusterRepository;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.config.ILocalEnvironment;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:44
 */
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_COMM)
public class PartnerClusterLoadManager implements ILoad {

    private Logger logger = LoggerFactory.getLogger(PartnerClusterLoadManager.class);

    @Autowired
    IPartnerClusterRepository partnerClusterRepository;

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    ILocalEnvironment localEnvironment;



    @Override
    public boolean load(){
        logger.info("PartnerClusterLoadManager start");

        Set<String> partners = null;

        //则取所有合作方，暂不按集群加载
        partners = partnerClusterRepository.queryAllPartner();

        partners.add(Constant.DEFAULT_PARTNER);

        partnerClusterCache.setPartners(partners);

        logger.info("PartnerClusterLoadManager success, size:"+partners.size());
        return true;
    }
}
