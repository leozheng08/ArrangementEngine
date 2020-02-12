package cn.tongdun.kunpeng.api.engine.model.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.api.engine.fp.Anomaly;
import cn.tongdun.kunpeng.api.engine.fp.ContainCheatingApps;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheatV2 extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(CheatV2.class);
//[
//    {
//        "name": "installedDangerAppCodes",
//            "type": "string",
//            "value": "多开"
//    },
//    {
//        "name": "runningDangerAppCodes",
//            "type": "string",
//            "value": "hookInline"
//    }
//]


    private String installedDangerAppCodes;
    private String runningDangerAppCodes;


    @Override
    public String getName() {
        return Constant.Function.ANDROID_IS_CHEAT_V2;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("installedDangerAppCodes", functionParam.getName())) {
                installedDangerAppCodes = functionParam.getValue();
            }
            else if (StringUtils.equals("runningDangerAppCodes", functionParam.getName())) {
                runningDangerAppCodes = functionParam.getValue();
            }

        });
    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;


        final List<Double> results = new ArrayList<>(1);

        // 1命中，0不命中
        double hitValue = 0D;

        final Map<String, Object> deviceInfo = context.getDeviceInfo();

        if (StringUtils.isBlank(installedDangerAppCodes) && StringUtils.isBlank(runningDangerAppCodes)) {
            logger.warn("the installedDangerAppCodes and runningDangerAppCodes are blank, platform=android, deviceInfo={}", deviceInfo);
            return new CalculateResult(false, null);
        }

        long start = System.currentTimeMillis();
        // FIXME: 2020-01-17 add util implemention
        ContainCheatingApps containCheatingApps = new ContainCheatingApps();
        long cost = System.currentTimeMillis() - start;
        if (cost > 150) {
            logger.warn("AnomalyUtil.getContainCheatingApps cost too long({}ms)", cost);
        }

        if (containCheatingApps == null) {
            logger.warn("cannot parse containCheatingApps(AnomalyUtil), platform=android, deviceInfo={}", deviceInfo);
            return new CalculateResult(false, null);
        }

//        AndroidCheatAppDetail detail = new AndroidCheatAppDetail();

        List<Anomaly> installedDangerApps = containCheatingApps.getInstalledDangerApps();
        if (CollectionUtils.isNotEmpty(installedDangerApps)) {
            for (Anomaly anomaly : installedDangerApps) {
                if (installedDangerAppCodes.contains(anomaly.getCode())) {
                    hitValue = 1D;
//                    detail.addInstalledDangerApp(anomaly.getCode());
                }
            }
        }

        List<Anomaly> runningDangerApps = containCheatingApps.getRunningDangerApps();
        if (CollectionUtils.isNotEmpty(runningDangerApps)) {
            for (Anomaly anomaly : runningDangerApps) {
                if (runningDangerAppCodes.contains(anomaly.getCode())) {
                    hitValue = 1D;
//                    detail.addRunningDangerApp(anomaly.getCode());
                }
            }
        }

//        if (hitValue > 0) {
//            detail.setFilterId(conditionId);
//            detail.setTempRuleUuid(ruleUuid);
//            context.putRuleDetail(detail);
//        }

//        results.add(hitValue);
//
//        return results;

        return new CalculateResult(hitValue, null);
    }
}
