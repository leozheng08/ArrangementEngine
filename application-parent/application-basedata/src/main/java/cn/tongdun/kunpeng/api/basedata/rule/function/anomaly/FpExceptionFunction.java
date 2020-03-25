package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.api.ruledetail.FpExceptionDetail;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FpExceptionFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(FpExceptionFunction.class);

    private String codes;
    private Map<String, String> fpResultMap;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_FP_EXCEPTION;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("anomaly FpException function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("codes", param.getName())) {
                codes = param.getValue();
            }
        });
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        String code;
        boolean success = DataUtil.toBoolean(deviceInfo.get("success"));
        if (success) {
            String result = (String) deviceInfo.get("exceptionInfo");
            if (result == null) {
                return new FunctionResult(false);
            }
            try {
                Map obj = JSON.parseObject(result, HashMap.class);
                code = (String) obj.get("code");
            }
            catch (Exception e) {
                return new FunctionResult(false);
            }
        }
        else {
            code = (String) deviceInfo.get("code");
        }

        if (code == null || "".equals(code)) {
            return new FunctionResult(false);
        }

        List<String> mycodes = Splitter.on(",").splitToList(codes);

        boolean ret = false;
        DetailCallable detailCallable = null;
        if (mycodes.contains(code) && null != fpResultMap.get(code)) {
            ret = true;
            detailCallable = () -> {
                FpExceptionDetail detail = new FpExceptionDetail();
                detail.setCode(code);
                detail.setCodeDisplayName(fpResultMap.get(code));
                return detail;
            };
        }

        return new FunctionResult(ret, detailCallable);
    }


}
