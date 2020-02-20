package cn.tongdun.kunpeng.api.basedata.rule.function.namelist;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.basedata.BaseDataContext;
import cn.tongdun.kunpeng.common.Constant;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by lvyadong on 2020/01/19.
 */
public class ComplexCustomListFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(ComplexCustomListFunction.class);
    private static final String COMMA_SEPARATOR = ",";
    private static final String SEMICOLON_SEPARATOR = ";";

    private String calcField;
    private String definitionList;
    private String matchMode;
    private CustomListValueCache customListValueCache;

    @Override
    public String getName() {
        return Constant.Function.WATCHLIST_COMPLEX_CUSTOM_LIST;
    }


    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("ComplexCustomList function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("calcField", param.getName())) {
                calcField = param.getValue();
            }
            else if (StringUtils.equals("definitionList", param.getName())) {
                definitionList = param.getValue();
            }
            else if (StringUtils.equals("matchMode", param.getName())) {
                matchMode = param.getValue();
            }
        });

        customListValueCache = (CustomListValueCache) SpringContextHolder.getBean("customListValueCache");

    }


    @Override
    public FunctionResult run(ExecuteContext context) {


            if (StringUtils.isBlank(calcField) || StringUtils.isBlank(matchMode)) {
                return new FunctionResult(false, null);
            }
            List<String> dimValueList = getDimValues((BaseDataContext) context, calcField);
            if (CollectionUtils.isEmpty(dimValueList)) {
                return new FunctionResult(false, null);
            }
            for (String dimValue : dimValueList) {
                Set<String> matchList = new HashSet<>();
                MatchModeEnum matchModeEnum = MatchModeEnum.valueOf(matchMode);
                if (MatchModeEnum.NOT_EQUALS.equals(matchModeEnum) || MatchModeEnum.EQUALS.equals(matchModeEnum)) {
                    if (isInCustomList(definitionList, dimValue)) {
                        matchList.add(replaceDimValue(dimValue));
                    }
                    if (MatchModeEnum.NOT_EQUALS.equals(matchModeEnum)) {
                        if (CollectionUtils.isEmpty(matchList)) {
                            return new FunctionResult(true, null);
                        } else {
                            return new FunctionResult(false, null);
                        }
                    }
                }
                if (CollectionUtils.isEmpty(matchList)) {
                    return new FunctionResult(true, null);
                } else {
                    return new FunctionResult(false, null);
                }
            }
        return new FunctionResult(false, null);
    }

    private List<String> getDimValues(BaseDataContext context, String calcField) {
        String[] calcFields = calcField.split(COMMA_SEPARATOR);
        Map<Integer, Set<String>> rowValues = new HashMap<>();
        int i = 0;
        for (String field : calcFields) {
            Set<String> value = new HashSet<>();
            List<String> dimValues = VelocityHelper.getDimensionValues(context, field);
            if (CollectionUtils.isEmpty(dimValues)) {
                return new ArrayList<>(0);
            }

            for (String dimValue : dimValues) {
                // 字符串分隔
                List<String> values = Lists.newArrayList(dimValue.split(","));
                for (String dim : values) {
                    if (StringUtils.isNotBlank(dim)) {
                        value.add(dim);
                    }
                }
            }
            // 存放按顺序逗号分隔匹配后的具体列表
            rowValues.put(i, value);
            i++;
        }

        List<Set<String>> sets = Lists.newArrayListWithCapacity(rowValues.size());
        for (int k = 0; k < rowValues.size(); k++) {
            sets.add(k, rowValues.get(k));
        }
        Set<String> values = new HashSet<>();
        // 笛卡尔积
        Set<List<String>> set = Sets.cartesianProduct(sets);
        for (List<String> characters : set) {
            values.add(StringUtils.join(characters, COMMA_SEPARATOR));
        }
        return Lists.newArrayList(values);
    }

    private boolean isInCustomList(String listNameUuid, String dimValue) {
        double score = customListValueCache.getZsetScore(listNameUuid, dimValue);
        return customListValueCache.isEffectiveValue(score, new Date());
    }

    private String replaceDimValue(String dimValue) {
        if (StringUtils.isBlank(dimValue)) {
            return null;
        }
        return dimValue.replaceAll(COMMA_SEPARATOR, SEMICOLON_SEPARATOR);
    }
}
