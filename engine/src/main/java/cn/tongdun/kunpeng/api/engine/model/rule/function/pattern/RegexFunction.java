package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.engine.model.rule.util.InterruptibleCharSequence;
import cn.tongdun.kunpeng.api.engine.model.rule.util.TimeSlice;
import cn.tongdun.kunpeng.api.engine.model.rule.util.VelocityHelper;
import cn.tongdun.kunpeng.api.ruledetail.RegexDetail;
import cn.tongdun.kunpeng.api.ruledetail.TimeDiffDetail;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.tdframework.core.concurrent.IThreadService;
import cn.tongdun.tdframework.core.concurrent.MDCUtil;
import cn.tongdun.tdframework.core.config.IConfigRepository;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: liuq
 * @Date: 2019/12/5 8:57 PM
 */
public class RegexFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(RegexFunction.class);

    private static final int THREAD_NUM = Runtime.getRuntime().availableProcessors() + 1;
    private static final int QUEUE_SIZE = 100;
    private static ThreadPoolExecutor regexThreadPool = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 10,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(QUEUE_SIZE),
            new ThreadFactoryBuilder().setNameFormat("regex-function-thread-%d").build());

    private String property;
    /**
     * 匹配模式是匹配(true)还是不匹配(false)
     */
    private boolean isMatch = true;

    /**
     * 正则表达式编译后的结果
     */
    private Pattern regexPattern;

    @Override
    public String getName() {
        return Constant.Function.PATTERN_REGEX;
    }

    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("LocalRegexFunction parse error,function params is blank!");
        }

        boolean ignoreCase = true;
        String regexString = null;
        for (FunctionParam functionParam : functionDesc.getParamList()) {
            if (StringUtils.equals("property", functionParam.getName())) {
                property = functionParam.getValue();
            }
            if (StringUtils.equals("result", functionParam.getName())) {
                isMatch = StringUtils.equals("1", functionParam.getValue());
            }
            if (StringUtils.equals("ignoreCase", functionParam.getName())) {
                ignoreCase = StringUtils.equals("1", functionParam.getValue());
            }
            if (StringUtils.equals("regex", functionParam.getName())) {
                regexString = functionParam.getValue();
            }
        }
        if (StringUtils.isBlank(regexString)) {
            throw new ParseException("LocalRegexFunction parse error,regexString is blank!");
        }
        int regexFlag = Pattern.DOTALL;
        if (ignoreCase) {
            regexFlag = regexFlag | Pattern.CASE_INSENSITIVE;
        }
        this.regexPattern = Pattern.compile(regexString, regexFlag);

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {

        List<String> dimValues = VelocityHelper.getDimensionValues((AbstractFraudContext)executeContext, property);
        if (null == dimValues || dimValues.isEmpty()) {
            return new FunctionResult(false);
        }

        // 匹配字段可选数组，采用多线程
        List<Callable<RegularMatchData>> tasks = Lists.newArrayListWithCapacity(dimValues.size());
        for (String dimValue : dimValues) {
            if(StringUtils.isBlank(dimValue)){
                continue;
            }
            tasks.add(MDCUtil.wrap(new AsynRegularMatch(dimValue)));
        }

        List<Future<RegularMatchData>> futures = null;
        try {
            futures = regexThreadPool.invokeAll(tasks, 100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("RegexFunction error", e);
        }
        if (null == futures || futures.isEmpty()) {
            return new FunctionResult(false);
        }

        Boolean ret = false;
        DetailCallable detailCallable = null;
        for(Future<RegularMatchData> future : futures) {
            RegularMatchData regularMatchData = null;
            try {
                regularMatchData = future.get(100, TimeUnit.MILLISECONDS);
                Boolean matchResult = regularMatchData.getResult();
                if (matchResult) {
                    String finalDimValue= regularMatchData.getDimValue();
                    String propertyDisplayName = VelocityHelper.getFieldDisplayName(property,(AbstractFraudContext) executeContext);
                    detailCallable = () -> {
                        RegexDetail detail = new RegexDetail();
                        detail.setRuleUuid(ruleUuid);
                        detail.setConditionUuid(conditionUuid);
                        detail.setDescription(description);
                        detail.setDimType(property);
                        detail.setDimTypeDisplayName(propertyDisplayName);
                        detail.setValue(finalDimValue);
                        return detail;
                    };
                    ret = true;
                    break;
                }
            } catch (Exception e) {
                logger.error("RegexFunction error", e);
                future.cancel(true);
            }
        }

        if (ret == null) {
            ret = false;
        }
        if (!isMatch) {
            //如果是不匹配
            return new FunctionResult(!ret, detailCallable);
        }
        return new FunctionResult(ret, detailCallable);
    }



    private class AsynRegularMatch implements Callable {

        private String dimValue;

        public AsynRegularMatch(String dimValue) {
            this.dimValue = dimValue;
        }

        @Override
        public RegularMatchData call() throws Exception {
            RegularMatchData regularMatchData = new RegularMatchData();
            Matcher matcher = regexPattern.matcher(new InterruptibleCharSequence(dimValue));
            Boolean result = matcher.matches();
            regularMatchData.setDimValue(dimValue);
            regularMatchData.setResult(result);
            return regularMatchData;
        }
    }
}
