package cn.tongdun.kunpeng.api.engine.model.rule.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: liuq
 * @Date: 2019/12/5 8:57 PM
 */
public class LocalRegexFunction extends AbstractFunction {

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
        return "localRegex";
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

        Object propertyField = executeContext.getField(property);
        if (propertyField == null) {
            return new FunctionResult(false);
        }
        String propertyString = propertyField.toString();
        Matcher matcher = regexPattern.matcher(propertyString);
        return new FunctionResult(matcher.matches());
    }
}