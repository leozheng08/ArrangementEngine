package cn.tongdun.kunpeng.api.basedata.rule.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.basedata.rule.function.anomaly.WebDeviceStatusAbnormalFunction;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.api.ruledetail.DeviceStatusAbnormalDetail;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author: liuq
 * @Date: 2020/5/27 2:43 下午
 */
public abstract class AbstractDeviceStatusAbnormalFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(WebDeviceStatusAbnormalFunction.class);
    protected Set<String> abnomalTagSet;

    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("anomaly DeviceStatusAbnormal function parse error,no params!");
        }
        abnomalTagSet = Sets.newHashSet();
        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("codes", param.getName())) {
                abnomalTagSet.addAll(Splitter.on(",").splitToList(param.getValue()));
            }
        });
    }

    public abstract String getAppType();

    /**
     * 目前小程序等价于web，所以要特殊判断
     * @param inheritAppType
     * @param deviceInfoAppType
     * @return
     */
    private boolean appTypeEqual(String inheritAppType,String deviceInfoAppType){
        if (StringUtils.equalsIgnoreCase(inheritAppType,deviceInfoAppType)){
            return true;
        }
        return StringUtils.equalsIgnoreCase(inheritAppType, "web");
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        if (deviceInfo == null || deviceInfo.isEmpty()) {
            return new FunctionResult(false);
        }

        boolean result = false;
        DetailCallable detailCallable = null;
        try {
            boolean success = DataUtil.toBoolean(deviceInfo.get("success"));
            if (success) {
                boolean flag = true;
                Collection<String> fpAbnormalTags = (Collection<String>) deviceInfo.get("abnormalTags");
                if ((null != deviceInfo.get("fmVersion") && deviceInfo.get("fmVersion").toString().compareToIgnoreCase("3.0.0") < 0) ||
                        (null != deviceInfo.get("version") && deviceInfo.get("version").toString().compareToIgnoreCase("3.0.0") < 0) ||
                        null == fpAbnormalTags) {
                    flag = false;
                }
                if (null != fpAbnormalTags && null != deviceInfo.get("appOs") && appTypeEqual(this.getAppType(), deviceInfo.get("appOs").toString())) {
                    flag = true;
                }
                if (flag) {
                    boolean isHitFlag = false;
                    List<String> hisTags = new ArrayList<>();
                    for (String tag : fpAbnormalTags) {
                        if (abnomalTagSet.contains(tag)) {
                            isHitFlag = true;
                            hisTags.add(tag);
                        }
                    }
                    if (isHitFlag) {
                        result = true;
                        detailCallable = () -> {
                            DeviceStatusAbnormalDetail detail = new DeviceStatusAbnormalDetail();
                            detail.setAbnormalTags(hisTags);
                            return detail;
                        };
                    }
                } else {
                    result = false;
                }
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
            logger.error(TraceUtils.getFormatTrace() + "[Abnormality] isDeviceStatusAbnormal", e);
        }

        return new FunctionResult(result, detailCallable);
    }
}
