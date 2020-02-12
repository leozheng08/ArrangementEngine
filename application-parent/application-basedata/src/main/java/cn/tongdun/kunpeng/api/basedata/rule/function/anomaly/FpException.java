package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class FpException extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(FpException.class);

    private String codes;
    private Map<String, String> fpResultMap;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_FP_EXCEPTION;
    }


    @Override
    public void parse(FunctionDesc functionDesc) {
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
    public Object eval(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        Map<String, Object> deviceInfo = context.getDeviceInfo();
        String code;
        boolean success = DataUtil.toBoolean(deviceInfo.get("success"));
        if (success) {
            String result = (String) deviceInfo.get("exceptionInfo");
            if (result == null) {
                return false;
            }
            try {
                JSONObject obj = JSONObject.parseObject(result);
                code = (String) obj.get("code");
            }
            catch (Exception e) {
                return false;
            }
        }
        else {
            code = (String) deviceInfo.get("code");
        }

        if (code == null || "".equals(code)) {
            return false;
        }

        List<String> mycodes = Splitter.on(",").splitToList(codes);
        if (mycodes.contains(code)) {
            // 详情
            String result = fpResultMap.get(code);
            if (result == null) {
                return false;
            }

            return true;
        }
        else {
            return false;
        }
    }


}
