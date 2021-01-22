package cn.tongdun.kunpeng.api.engine.model.rule.function.namelist;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.engine.model.rule.util.VelocityHelper;
import cn.tongdun.kunpeng.api.ruledetail.CustomListDetail;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.share.json.JSON;
import com.eclipsesource.json.Json;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lvyadong on 2020/01/14.
 */
public class CustomListFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(CustomListFunction.class);

    private String calcField;
    private String definitionList;
    private String matchMode;
    private CustomListValueCache customListValueCache;

    @Override
    public String getName() {
        return Constant.Function.WATCHLIST_CUSTOM_LIST;
    }

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("CustomList function parse error,no params!");
        }
        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("calcField", param.getName())) {
                calcField = param.getValue();
            } else if (StringUtils.equals("definitionList", param.getName())) {
                definitionList = param.getValue();
            } else if (StringUtils.equals("matchMode", param.getName())) {
                matchMode = param.getValue();
            }
        });
        customListValueCache = SpringContextHolder.getBean("customListValueCache", CustomListValueCache.class);
    }

    @Override
    public FunctionResult run(ExecuteContext context) {
        List<String> dimValueList = VelocityHelper.getDimensionValues((AbstractFraudContext) context, calcField);
        if (CollectionUtils.isNotEmpty(dimValueList)) {
            for (String dim : dimValueList) {
                List<String> dims = Lists.newArrayList(dim.split(","));
                Set<String> matchList = new HashSet<>();
                MatchModeEnum matchModeEnum = MatchModeEnum.valueOf(matchMode);
                logger.info("matchModeEnum:"+matchModeEnum+"---"+JSON.toJSONString(dims));
                if (MatchModeEnum.EQUALS.equals(matchModeEnum) || MatchModeEnum.NOT_EQUALS.equals(matchModeEnum)) {
                    for (String dimValue : dims) {
                        processOneDimValue(definitionList, dimValue, matchList);
                    }
                    if (MatchModeEnum.NOT_EQUALS.equals(matchModeEnum)) {
                        if (CollectionUtils.isEmpty(matchList)) {
                            return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList, dim));
                        } else {
                            return new FunctionResult(false, null);
                        }
                    } else {
                        if (CollectionUtils.isNotEmpty(matchList)) {
                            return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList, dim));
                        } else {
                            return new FunctionResult(false, null);
                        }
                    }
                } else {
                    List<String> listDataList = customListValueCache.get(definitionList);
                    if(listDataList == null){
                        return new FunctionResult(false, null);
                    }
                    for (String dimValue : dims) {
                        for (String data : listDataList) {
                            if (match(dimValue, data, matchModeEnum)) {
                                matchList.add(data);
                            }
                        }
                    }
                    if (MatchModeEnum.EXCLUDE.equals(matchModeEnum)) {
                        if (CollectionUtils.isEmpty(matchList)) {
                            return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList, dim));
                        } else {
                            return new FunctionResult(false, null);
                        }
                    } else {
                        if (CollectionUtils.isNotEmpty(matchList)) {
                            return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList, dim));
                        } else {
                            return new FunctionResult(false, null);
                        }
                    }
                }
            }
        }
        return new FunctionResult(false, null);
    }

    private DetailCallable buildConditionDetail(AbstractFraudContext context, Set<String> matchList, String dimValue) {
        return () -> {
            CustomListDetail detail = new CustomListDetail();
            detail.setRuleUuid(this.ruleUuid);
            detail.setConditionUuid(this.conditionUuid);
            if (CollectionUtils.isEmpty(matchList)) {
                detail.setList(null);
            } else {
                detail.setList(Lists.newLinkedList(matchList));
            }
            detail.setDimType(this.calcField);
            detail.setDimTypeDisplayName(VelocityHelper.getFieldDisplayName(this.calcField, context));
            detail.setDescription(description);
            detail.setDimValue(dimValue);
            return detail;
        };
    }

    private boolean match(String string, String matchString, MatchModeEnum matchMode) {
        switch (matchMode) {
            case INCLUDE:
            case EXCLUDE:
                return StringUtils.contains(string, matchString);
            case PREFIX:
                return StringUtils.startsWith(string, matchString);
            case SUFFIX:
                return StringUtils.endsWith(string, matchString);
            case FUZZY:
                return fuzzyMatch(string, matchString);
            default:
                return false;
        }
    }

    /**
     * 匹配的时候允许中间夹杂其他字符。 比如“———办——证———”，用“办证”匹配返回的是true。
     */
    private boolean fuzzyMatch(String text, String pattern) {
        int from = 0;
        for (char c : pattern.toCharArray()) {
            from = text.indexOf(c, from);
            if (from < 0) {
                return false;
            }
            from++;
        }
        return true;
    }

    private void processOneDimValue(String listNameUuid, String dimValue, Set<String> matchList) {
        logger.info("matchList");
        if (isInCustomList(listNameUuid, dimValue)) {
            matchList.add(dimValue);
        }
        logger.info("matchList:"+ JSON.toJSONString(matchList)+"---"+dimValue);
    }

    private boolean isInCustomList(String listNameUuid, String dimValue) {
        double score = customListValueCache.getZsetScore(listNameUuid, dimValue);
        boolean flag=customListValueCache.isEffectiveValue(score, new Date());
        logger.info("isInCustomList"+flag);
        return flag;
    }

    public static void main(String[] args) {
        double score=1.597291199E12;
        System.out.println(score >= System.currentTimeMillis());
    }
}
