package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.rule.util.VelocityHelper;
import cn.tongdun.kunpeng.api.ruledetail.CustomListDetail;
import cn.tongdun.kunpeng.api.ruledetail.PersonalFuzzyListDetail;
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
public class PersonalFuzzyListFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(PersonalFuzzyListFunction.class);
    private static final String COMMA_SEPARATOR = ",";
    private static final String SEMICOLON_SEPARATOR = ";";

    private String definitionList;
    private String name;
    private String nameWeight;
    private String birthday;
    private String birthdayWeight;
    private String similarity;
    private String gender;
    private Integer isMatch;

    private CustomListValueCache customListValueCache;

    @Override
    public String getName() {
        return Constant.Function.WATCHLIST_PERSONAL_FUZZY_LIST;
    }


    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("ComplexCustomList function parse error,no params!");
        }
        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("definitionList", param.getName())) {
                definitionList = param.getValue();
            } else if (StringUtils.equals("name", param.getName())) {
                name = param.getValue();
            } else if (StringUtils.equals("nameWeight", param.getName())) {
                nameWeight = param.getValue();
            } else if (StringUtils.equals("birthday", param.getName())) {
                birthday = param.getValue();
            } else if (StringUtils.equals("birthdayWeight", param.getName())) {
                birthdayWeight = param.getValue();
            } else if (StringUtils.equals("similarity", param.getName())) {
                similarity = param.getValue();
            } else if (StringUtils.equals("gender", param.getName())) {
                gender = param.getValue();
            } else if (StringUtils.equals("isMatch", param.getName())) {
                isMatch = Integer.valueOf(param.getValue());
            }
        });
        customListValueCache = SpringContextHolder.getBean("customListValueCache", CustomListValueCache.class);
    }


    /**
     * <P>
     * 1、先精确匹配性别，如果性别为空值则认为满足匹配，继续进行其他字段的匹配；
     * 2、然后进行姓名和生日的相似度匹配，逻辑为：（姓名全名相似度 A * 设定权重Y1 + 生日精确匹配*设定权重 Y2） >= 设置相似度X；
     *    Y1+Y2=100%；
     * 3、生日精确匹配，相似度为0或100；
     * 4、模糊匹配使用编辑距离算法
     * </P>
     *
     * @param context
     * @return
     */
    @Override
    public FunctionResult run(ExecuteContext context) {
        if (StringUtils.isAnyBlank(name, nameWeight, birthday, birthdayWeight, similarity)) {
            return new FunctionResult(false, null);
        }
        Map<Integer, List<String>> rowValues = new HashMap<>();
        List<String> dimValueList = getDimValues((AbstractFraudContext) context, gender, rowValues);
        if (CollectionUtils.isEmpty(dimValueList)) {
            return new FunctionResult(false, null);
        }
//        for (String dimValue : dimValueList) {
//            Set<String> matchList = new HashSet<>();
//            MatchModeEnum matchModeEnum = MatchModeEnum.valueOf(matchMode);
//            if (MatchModeEnum.NOT_EQUALS.equals(matchModeEnum) || MatchModeEnum.EQUALS.equals(matchModeEnum)) {
//                if (isInCustomList(definitionList, dimValue)) {
//                    matchList.add(replaceDimValue(dimValue));
//                }
//                if (MatchModeEnum.NOT_EQUALS.equals(matchModeEnum)) {
//                    if (CollectionUtils.isEmpty(matchList)) {
//                        return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList, dimValue, rowValues));
//                    } else {
//                        return new FunctionResult(false, null);
//                    }
//                }
//            }
//            if (CollectionUtils.isNotEmpty(matchList)) {
//                return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList, dimValue, rowValues));
//            } else {
//                return new FunctionResult(false, null);
//            }
//        }
        return new FunctionResult(false, null);
    }

    private DetailCallable buildConditionDetail(AbstractFraudContext context, Set<String> matchList, String dimValue, Map<Integer, List<String>> rowValues) {
        String newDimValue = getOriginDim(rowValues, dimValue);
        return () -> {
            PersonalFuzzyListDetail detail = new PersonalFuzzyListDetail();
            detail.setRuleUuid(this.ruleUuid);
            detail.setConditionUuid(this.conditionUuid);
            if (CollectionUtils.isEmpty(matchList)) {
                detail.setList(null);
            } else {
                detail.setList(Lists.newLinkedList(matchList));
            }
            detail.setDescription(description);
            detail.setName(this.name);
            detail.setBirthday(this.birthday);
            detail.setGender(this.gender);

            return detail;
        };
    }

    private List<String> getDimValues(AbstractFraudContext context, String calcField, Map<Integer, List<String>> dims) {
        String[] calcFields = calcField.split(COMMA_SEPARATOR);
        Map<Integer, Set<String>> rowValues = new HashMap<>();
        int i = 0;
        for (String field : calcFields) {
            Set<String> value = new HashSet<>();
            List<String> dimValues = VelocityHelper.getDimensionValues(context, field);
            if (CollectionUtils.isEmpty(dimValues)) {
                return new ArrayList<>(0);
            }
            // 按顺序存放每个字段匹配值
            dims.put(i, dimValues);

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

    private String getOriginDim(Map<Integer, List<String>> rowValues, String dimValue) {
        String[] values = dimValue.split(COMMA_SEPARATOR);
        List<String> origin = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            List<String> originList = rowValues.get(i);
            String o = getDimValue(originList, values[i]);
            if (StringUtils.isNotBlank(o)) {
                origin.add(o);
            }
        }
        if (origin.size() != values.length) {
            return replaceDimValue(dimValue);
        }
        return StringUtils.join(origin, SEMICOLON_SEPARATOR);
    }

    private String getDimValue(List<String> originList, String value) {
        for (String o : originList) {
            List<String> origins = Lists.newArrayList(o.split(COMMA_SEPARATOR));
            if (origins.contains(value)) {
                return o;
            }
        }
        return null;
    }

    private String getDimTypeName(String dimType, AbstractFraudContext context){
        List<String> dimTypeList = Lists.newArrayList(dimType.split(COMMA_SEPARATOR));
        List<String> nameList = new ArrayList<>(8);
        for(String type:dimTypeList){
            String name = VelocityHelper.getFieldDisplayName(type,context);
            nameList.add(name);
        }
        return String.join(COMMA_SEPARATOR, nameList);
    }

    private String replaceDimValue(String dimValue) {
        if (StringUtils.isBlank(dimValue)) {
            return null;
        }
        return dimValue.replaceAll(COMMA_SEPARATOR, SEMICOLON_SEPARATOR);
    }
}
