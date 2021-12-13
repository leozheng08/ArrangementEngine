package cn.tongdun.kunpeng.api.basedata.rule.function.location;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.fraudmetrix.sphinx.client.service.ValidateTokenService;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.ruledetail.ValidateTokenDetail;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Arrays;
import java.util.List;

public class VerificationCodeFunction extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeFunction.class);
    String codes;

    @Autowired
    private ValidateTokenService validateTokenService;

    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;
        if (org.apache.commons.lang.StringUtils.isBlank(codes)) {
            return new FunctionResult(false);
        }
        List<String> checkboxs = Arrays.asList(codes.split(","));
        Object validateToken = context.get("validateToken");  //字段固定，validateToken即代表验证码
        if (validateToken == null || org.apache.commons.lang.StringUtils.isBlank(validateToken.toString())) {
            if (checkboxs.contains("isDeviceIdBlank")) {
                return new FunctionResult(true, genDetail("isDeviceIdBlank"));
            } else {
                return new FunctionResult(false);
            }
        }
        boolean result = false;
        try {
            String deviceId = "";
            if (null != context.get("deviceId")) {
                deviceId = context.get("deviceId").toString();
            }
            result = validateTokenService.validateTokenByDeviceId(validateToken.toString(), context.getPartnerCode(), deviceId);
        } catch (Exception e) {
            logger.error("validateTokenService = ", e);
        }
        if (!result) {
            if (checkboxs.contains("isValidFalse")) {
                return new FunctionResult(true, genDetail("isValidFalse"));
            }
        }
        return new FunctionResult(false);
    }

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("VerificationCode function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("codes", param.getName())) {
                codes = param.getValue();
            }
        });
    }

    private DetailCallable genDetail(String code) {
        DetailCallable detailCallable = () -> {
            ValidateTokenDetail validateTokenDetail = new ValidateTokenDetail();
            if (code.equalsIgnoreCase("isValidFalse")) {
                validateTokenDetail.setIsValidFalse(true);
            } else {
                validateTokenDetail.setIsDeviceIdBlank(true);
            }
            validateTokenDetail.setConditionUuid(this.conditionUuid);
            validateTokenDetail.setRuleUuid(ruleUuid);
            return validateTokenDetail;
        };
        return detailCallable;
    }

    @Override
    public String getName() {
        return Constant.Function.VERIFICATION_CODE_ABNORMAL;
    }
}
