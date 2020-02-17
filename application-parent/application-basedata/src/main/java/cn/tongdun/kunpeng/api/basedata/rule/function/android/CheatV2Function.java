package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.basedata.service.fp.Anomaly;
import cn.tongdun.kunpeng.api.basedata.service.fp.ContainCheatingApps;
import cn.tongdun.kunpeng.api.ruledetail.AndroidCheatAppDetail;
import cn.tongdun.kunpeng.common.Constant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheatV2Function extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(CheatV2Function.class);

    private String installedDangerAppCodes;
    private String runningDangerAppCodes;


    @Override
    public String getName() {
        return Constant.Function.ANDROID_IS_CHEAT_V2;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("android CheatV2 function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("installedDangerAppCodes", param.getName())) {
                installedDangerAppCodes = param.getValue();
            }
            else if (StringUtils.equals("runningDangerAppCodes", param.getName())) {
                runningDangerAppCodes = param.getValue();
            }
        });
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;


        final List<Double> results = new ArrayList<>(1);

        // 1命中，0不命中
        double hitValue = 0D;

        final Map<String, Object> deviceInfo = context.getDeviceInfo();

        if (StringUtils.isBlank(installedDangerAppCodes) && StringUtils.isBlank(runningDangerAppCodes)) {
            logger.warn("the installedDangerAppCodes and runningDangerAppCodes are blank, platform=android, deviceInfo={}", deviceInfo);
            return new FunctionResult(false);

        }

        long start = System.currentTimeMillis();
        // FIXME: 2020-01-17 add util implementation
        ContainCheatingApps containCheatingApps = new ContainCheatingApps();
        long cost = System.currentTimeMillis() - start;
        if (cost > 150) {
            logger.warn("AnomalyUtil.getContainCheatingApps cost too long({}ms)", cost);
        }

        if (containCheatingApps == null) {
            logger.warn("cannot parse containCheatingApps(AnomalyUtil), platform=android, deviceInfo={}", deviceInfo);
            return new FunctionResult(false);
        }

//        AndroidCheatAppDetail detail = new AndroidCheatAppDetail();


        List<Anomaly> installedDangerApps = containCheatingApps.getInstalledDangerApps();
        List<String> installedApps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(installedDangerApps)) {
            for (Anomaly anomaly : installedDangerApps) {
                if (installedDangerAppCodes.contains(anomaly.getCode())) {
                    hitValue = 1D;
//                    detail.addInstalledDangerApp(anomaly.getCode());
                    installedApps.add(anomaly.getCode());
                }
            }
        }

        List<Anomaly> runningDangerApps = containCheatingApps.getRunningDangerApps();
        List<String> runningApps = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(runningDangerApps)) {
            for (Anomaly anomaly : runningDangerApps) {
                if (runningDangerAppCodes.contains(anomaly.getCode())) {
                    hitValue = 1D;
//                    detail.addRunningDangerApp(anomaly.getCode());
                    runningApps.add(anomaly.getCode());
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


        AndroidCheatAppDetail detail = null;
        if (1 == hitValue) {
            detail = new AndroidCheatAppDetail();

            if (CollectionUtils.isNotEmpty(installedApps)) {
                detail.setInstalledDangerApps(installedApps);
            }
            if (CollectionUtils.isNotEmpty(runningApps)) {
                detail.setRunningDangerApps(runningApps);
            }
        }

        return new FunctionResult(hitValue, detail);
    }


}
