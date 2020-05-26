package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.api.engine.model.rule.util.DataUtil;
import cn.tongdun.kunpeng.api.ruledetail.FpExceptionDetail;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WebFpFetchExceptionFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(WebFpFetchExceptionFunction.class);

    private Set<String> codeSet;


    @Override
    public String getName() {
        return Constant.Function.ANOMALY_FP_EXCEPTION;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("anomaly FpException function parse error,no params!");
        }
        codeSet = Sets.newHashSet();
        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("codes", param.getName())) {
                if (StringUtils.isNotBlank(param.getValue())) {
                    codeSet.addAll(Splitter.on(",").splitToList(param.getValue()));
                }
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
            String exceptionInfo = (String) deviceInfo.get("exceptionInfo");
            if (exceptionInfo == null) {
                return new FunctionResult(false);
            }
            try {
                Map obj = JSON.parseObject(exceptionInfo, HashMap.class);
                code = (String) obj.get("code");
            } catch (Exception e) {
                logger.error("FpExceptionFunction run error,result:" + exceptionInfo, e);
                return new FunctionResult(false);
            }
        } else {
            code = (String) deviceInfo.get("code");
        }

        if (code == null || "".equals(code)) {
            return new FunctionResult(false);
        }

        boolean ret = false;
        DetailCallable detailCallable = null;
        DictionaryManager dictionaryManager= (DictionaryManager) SpringContextHolder.getBean("dictionaryManager");
        if (codeSet.contains(code) && null != dictionaryManager.getFpResultMap().get(code)) {
            ret = true;
            detailCallable = () -> {
                FpExceptionDetail detail = new FpExceptionDetail();
                detail.setCode(code);
                detail.setCodeDisplayName(dictionaryManager.getFpResultMap().get(code));
                return detail;
            };
        }

        return new FunctionResult(ret, detailCallable);
    }


}
