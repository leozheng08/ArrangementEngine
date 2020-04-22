package cn.tongdun.kunpeng.api.engine.model.policyindex;

import cn.fraudmetrix.module.tdrule.function.Function;
import cn.tongdun.kunpeng.api.engine.IExecutor;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Void execute(String policyUuid, AbstractFraudContext context) {

        List<PolicyIndex> policyIndexList = policyIndexCache.getList(policyUuid);
        if (null == policyIndexList || policyIndexList.isEmpty()) {
            return null;
        }
        for (PolicyIndex policyIndex : policyIndexList) {
            Function calculateFunction = policyIndex.getCalculateFunction();
            if (null == calculateFunction) {
                continue;
            }
            Object object = calculateFunction.eval(context);
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
            logger.error("PolicyIndexManager convert2Double error,valueString:" + valueStr + ",policyIndexUuid:" + policyIndex.getUuid());
            throw e;
        }
    }
}
