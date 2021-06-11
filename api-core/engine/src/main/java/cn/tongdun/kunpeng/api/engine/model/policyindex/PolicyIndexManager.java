package cn.tongdun.kunpeng.api.engine.model.policyindex;

import cn.fraudmetrix.module.tdrule.function.Function;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: liuq
 * @Date: 2020/2/18 1:14 PM
 */
@Component
public class PolicyIndexManager implements IExecutor<String, Void> {

    private static Logger logger = LoggerFactory.getLogger(PolicyIndexManager.class);

    @Autowired
    private PolicyIndexCache policyIndexCache;

    @Value("${open.debug.log:false}")
    private boolean openDebugLog;

    @Override
    public Void execute(String policyUuid, AbstractFraudContext context) {

        List<PolicyIndex> policyIndexList = policyIndexCache.getList(policyUuid);
        if (openDebugLog) {
            logger.info("PolicyIndexManager execute start.....policyUuid={}, policyIndexList={}", policyUuid, JSON.toJSONString(policyIndexList));
        }
        if (null == policyIndexList || policyIndexList.isEmpty()) {
            return null;
        }
        for (PolicyIndex policyIndex : policyIndexList) {
            Function calculateFunction = policyIndex.getCalculateFunction();
            if (null == calculateFunction) {
                continue;
            }
            Object object = calculateFunction.eval(context);
            if (openDebugLog) {
                logger.info("PolicyIndexManager execute .....policyUuid={}, policyIndex={}, result={}", policyUuid, JSON.toJSONString(policyIndex), object);
            }
            if (null != object) {
                context.putPolicyIndex(policyIndex.getUuid(), convert2Double(object, policyIndex));
            }

        }
        return null;
    }

    private Double convert2Double(Object indexValue, PolicyIndex policyIndex) {
        if (null == indexValue) {
            return null;
        }
        if (indexValue instanceof Double) {
            return (Double) indexValue;
        }
        String valueStr = indexValue.toString();
        try {
            return Double.parseDouble(valueStr);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "PolicyIndexManager convert2Double error,valueString:" + valueStr + ",policyIndexUuid:" + policyIndex.getUuid());
            throw e;
        }
    }
}
