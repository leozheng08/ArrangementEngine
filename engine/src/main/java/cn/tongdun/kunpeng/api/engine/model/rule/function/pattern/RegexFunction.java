package cn.tongdun.kunpeng.api.engine.model.rule.function.pattern;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.api.engine.model.rule.util.InterruptibleCharSequence;
import cn.tongdun.kunpeng.api.engine.model.rule.util.VelocityHelper;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: liuq
 * @Date: 2019/12/5 8:57 PM
 */
public class RegexFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(RegexFunction.class);
    private static final int THREAD_NUM = Runtime.getRuntime().availableProcessors() + 1;
    private static final int QUEUE_SIZE = 10_000;

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

//        List<String> dimValues = VelocityHelper.getDimensionValues((AbstractFraudContext)executeContext, property);
//        if (null == dimValues || dimValues.isEmpty()) {
//            return new FunctionResult(false);
//        }
//
//        List<Future<RegularMatchData>> futures = new ArrayList<>();
//        for(String dimValue :dimValues){
//            if(StringUtils.isBlank(dimValue)){
//                continue;
//            }
//
//            Future<RegularMatchData> future = regexThreadPool.submit(() -> {
//                RegularMatchData regularMatchData = new RegularMatchData();
//                Matcher matcher = regexPattern.matcher(new InterruptibleCharSequence(dimValue));
//                Boolean result = matcher.matches();
//                regularMatchData.setDimValue(dimValue);
//                regularMatchData.setResult(result);
//                return regularMatchData;
//            });
//            futures.add(future);
//        }
//
//
//        Boolean ret = false;
//
//            for(Future<RegularMatchData> future : futures) {
//                try {
//                    RegularMatchData regularMatchData = future.get(100, TimeUnit.MILLISECONDS);
//                } catch (Exception e) {
//                    logger.error(e.getMessage(), e);
//                    future.cancel(true);
//                }
//
//            }
//
//        if (ret == null) {
//            ret = false;
//        }


        Object propertyField = executeContext.getField(property);
        if (propertyField == null) {
            return new FunctionResult(false);
        }
        String propertyString = propertyField.toString();
        Future<Boolean> future = regexThreadPool.submit(() -> {
            Matcher matcher = regexPattern.matcher(new InterruptibleCharSequence(propertyString));
            return matcher.matches();
        });
        Boolean ret = null;
        try {
            ret = future.get(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            future.cancel(true);
        }
        if (ret == null) {
            ret = false;
        }
        if (!isMatch) {
            //如果是不匹配
            return new FunctionResult(!ret);
        }
        return new FunctionResult(ret);
    }
}
