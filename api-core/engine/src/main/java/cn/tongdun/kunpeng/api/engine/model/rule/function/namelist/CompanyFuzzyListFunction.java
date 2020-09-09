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
import cn.tongdun.kunpeng.api.engine.util.LevenshteinDistanceUtils;
import cn.tongdun.kunpeng.api.ruledetail.CompanyFuzzyListDetail;
import cn.tongdun.kunpeng.api.ruledetail.PersonalFuzzyListDetail;
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
 * 企业模糊匹配列表规则函数
 */
public class CompanyFuzzyListFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(CompanyFuzzyListFunction.class);
    private static final String COMMA_SEPARATOR = ",";
    private static final String SEMICOLON_SEPARATOR = ";";

    private String definitionList;
    private String nameField;
    private String registerNoField;
    private Double nameSimilarity;
    private Boolean isMatch;

    private String name;
    private String registerNo;

    private CustomListValueCache customListValueCache;

    @Override
    public String getName() {
        return Constant.Function.WATCHLIST_COMPANY_FUZZY_LIST;
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
                nameField = param.getValue();
            } else if (StringUtils.equals("no", param.getName())) {
                registerNoField = param.getValue();
            } else if (StringUtils.equals("nameSimilarity", param.getName())) {
                nameSimilarity = Double.valueOf(param.getValue()) / 100;
            } else if (StringUtils.equals("isMatch", param.getName())) {
                isMatch = StringUtils.equals(param.getValue(), "1");
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
        if (StringUtils.isAnyBlank(nameField, registerNoField)) {
            return new FunctionResult(false, null);
        }
        List<String> dataList = customListValueCache.get(this.definitionList);
        if(dataList == null){
            return new FunctionResult(false, null);
        }

        this.registerNo = (String)VelocityHelper.getDimensionValue((AbstractFraudContext) context, this.registerNoField);
        this.name = (String)VelocityHelper.getDimensionValue((AbstractFraudContext) context, this.nameField);

        Set<String> matchList = new HashSet<>();
        for(String dataValue: dataList){
            boolean matchResult = isMatch((AbstractFraudContext) context, this.definitionList, dataValue);
            if(matchResult){
                matchList.add(dataValue);
            }
        }
        if(this.isMatch){
            if(CollectionUtils.isNotEmpty(matchList)){
                return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList));
            } else {
                return new FunctionResult(false, null);
            }
        }else {
            if(CollectionUtils.isEmpty(matchList)){
                return new FunctionResult(true, buildConditionDetail((AbstractFraudContext) context, matchList));
            } else {
                return new FunctionResult(false, null);
            }
        }
    }

    private boolean isMatch(AbstractFraudContext context, String listNameUuid, String data){
//        double score = customListValueCache.getZsetScore(listNameUuid, data);
//        boolean flag = customListValueCache.isEffectiveValue(score, new Date());
//        if(!flag){
//            return false;
//        }

        String[] valueArr = data.split(COMMA_SEPARATOR);
        String registerNoValue = valueArr[0];
        String nameValue = valueArr[1];
        if(StringUtils.isNotBlank(this.registerNo)){
            if(StringUtils.equals(this.registerNo, registerNoValue)){
                return true;
            } else {
                return false;
            }
        }else {
            double tempSimilarity = LevenshteinDistanceUtils.editDistanceSimilary(this.name, nameValue);
            if(tempSimilarity >= this.nameSimilarity){
                return true;
            } else {
                return false;
            }
        }
    }

    private DetailCallable buildConditionDetail(AbstractFraudContext context, Set<String> matchList) {
        return () -> {
            CompanyFuzzyListDetail detail = new CompanyFuzzyListDetail();
            detail.setRuleUuid(this.ruleUuid);
            detail.setConditionUuid(this.conditionUuid);
            if (CollectionUtils.isEmpty(matchList)) {
                detail.setList(null);
            } else {
                detail.setList(Lists.newLinkedList(matchList));
            }
            detail.setDescription(description);
            detail.setName(this.name);
            detail.setNo(this.registerNo);
            detail.setNameSimilarity(String.valueOf(this.nameSimilarity));
            return detail;
        };
    }
}
