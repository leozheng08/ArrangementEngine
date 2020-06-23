package cn.tongdun.kunpeng.api.engine.load.step;

import cn.tongdun.kunpeng.api.engine.load.ILoad;
import cn.tongdun.kunpeng.api.engine.load.LoadPipeline;
import cn.tongdun.kunpeng.api.engine.model.cluster.PartnerClusterCache;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.IAccessBusinessRepository;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author: yuanhang
 * @date: 2020-06-10 14:31
 * 业务接入相关缓存数据
 **/
@Component
@Step(pipeline = LoadPipeline.NAME, phase = LoadPipeline.LOAD_COMM)
public class AccessBusinessLoadManager implements ILoad {

    @Autowired
    AccessBusinessCache accessBusinessCache;

    @Autowired
    PartnerClusterCache partnerClusterCache;

    @Autowired
    IAccessBusinessRepository accessBusinessRepository;



    private Logger logger = LoggerFactory.getLogger(AccessBusinessLoadManager.class);


    @Override
    public boolean load() {
        logger.info(TraceUtils.getFormatTrace() + " load accessBusiness start");
        long start = System.currentTimeMillis();
        List<AccessBusiness> accessBusinessList = accessBusinessRepository.queryAllUsableAccess(partnerClusterCache.getPartners());
        Map<String, List<AccessBusiness>> accessBusinessMap = accessBusinessList.stream().collect(groupingBy(r -> r.getPartnerCode() + ":" + r.getAppName()));
        for (Map.Entry<String, List<AccessBusiness>> entry : accessBusinessMap.entrySet()) {
            entry.getValue().stream().forEach(r -> accessBusinessCache.put(entry.getKey(), r));
        }
        logger.info(TraceUtils.getFormatTrace() + "load accessBusiness end cost time :{}", System.currentTimeMillis() - start);
        return true;
    }
}
