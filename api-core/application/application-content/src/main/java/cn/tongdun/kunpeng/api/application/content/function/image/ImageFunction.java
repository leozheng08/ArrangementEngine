package cn.tongdun.kunpeng.api.application.content.function.image;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.CompareUtils;
import cn.tongdun.kunpeng.api.ruledetail.ImageDetail;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 内容安全-图片识别
 * @author: zhongxiang.wang
 * @date: 2021-02-20 16:30
 */
public class ImageFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(ImageFunction.class);

    public static final String PARAM_KEY_CONDITIONS = "conditions";
    public static final String PARAM_KEY_LOGICOPERATOR = "logicOperator";
    public static final String PARAM_KEY_IMAGE_LOGO_MODEL_RESULT = "imageLogoModelResult";
    public static final String PARAM_KEY_IMAGE_LOGO_MODEL_RESULT2 = "image_logo_model_result";

    private String conditions;
    private String logicOperator;

    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        FunctionResult functionResult = new FunctionResult(false, null);
        AbstractFraudContext context = (AbstractFraudContext) executeContext;
        Object result = null == context.get(PARAM_KEY_IMAGE_LOGO_MODEL_RESULT) ?
                context.get(PARAM_KEY_IMAGE_LOGO_MODEL_RESULT2) : context.get(PARAM_KEY_IMAGE_LOGO_MODEL_RESULT);
        String logoModelResult = null == result ? null : result.toString();

        if (hasNullData(logoModelResult, conditions, logicOperator)) {
            return functionResult;
        }
        //解析图片logo及分数信息
        List<Map> imageLogoScores = this.parseLogoModelResult(logoModelResult);
        if (CollectionUtils.isEmpty(imageLogoScores)) {
            return functionResult;
        }
        //命中的策略条件
        List<List<FilterConditionDO>> hitFilters = new ArrayList<>();
        //解析策略条件
        List<List<FilterConditionDO>> filters = this.parseParams(conditions);

        //满足所有的条件
        if ("&&".equals(logicOperator)) {
            Integer sum = 0;
            a:
            for (List<FilterConditionDO> filter : filters) {
                b:
                for (Map imageLogoScore : imageLogoScores) {
                    if (this.isMatchLogoAndScore(filter, imageLogoScore)) {
                        sum++;
                        break b;
                    }
                }
            }
            //每一个条件 都有结果可以命中
            if (sum.equals(filters.size())) {
                hitFilters.addAll(filters);
                functionResult.setResult(true);
                functionResult.setDetailCallable(this.buildDetail(hitFilters));
            } else {
                functionResult.setResult(false);
            }
        }

        //满足任意条件
        if ("||".equals(logicOperator)) {
            a:
            for (List<FilterConditionDO> filter : filters) {
                b:
                for (Map imageLogoScore : imageLogoScores) {
                    if (this.isMatchLogoAndScore(filter, imageLogoScore)) {
                        hitFilters.add(filter);
                        functionResult.setResult(true);
                        functionResult.setDetailCallable(this.buildDetail(hitFilters));
                        break a;
                    }
                }
            }
        }


        return functionResult;
    }

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException(TraceUtils.getFormatTrace() + "ImageFunction parseFunction error:function or ParamList is null");
        }
        functionDesc.getParamList().stream().forEach(param -> {
            if (PARAM_KEY_CONDITIONS.equals(param.getName())) {
                conditions = param.getValue();
            }
            if (PARAM_KEY_LOGICOPERATOR.equals(param.getName())) {
                logicOperator = param.getValue();
            }
        });

    }

    @Override
    public String getName() {
        return Constant.Function.CONTENT_IMAGE;
    }

    /**
     * 校验数据
     *
     * @param logoModelResult 图片分析结果
     * @param conditions      条件
     * @param logicOperator   条件组之间的关系 && ||
     * @return
     */
    private boolean hasNullData(String logoModelResult, String conditions, String logicOperator) {
        if (StringUtils.isEmpty(logoModelResult)) {
            logger.warn("The imageLogoModelResult is empty!");
            return true;
        }
        if (StringUtils.isEmpty(conditions)) {
            logger.warn("The logo's rule condition is empty!");
            return true;
        }
        if (StringUtils.isEmpty(logicOperator)) {
            logger.error("The relation of the filters not defined !");
            return true;
        }
        return false;
    }

    /**
     * 解析图片logo及分数信息
     *
     * @param logoModelResult 图片解析结果json
     *                        eg:[{"score": 0.9977513551712036,"logoName": "chanel"},{"score": 0.9977513551712126,"logoName": "lv"},{"score": 0.3477513551712036,"logoName": "lv"}]
     * @return
     */
    public List<Map> parseLogoModelResult(String logoModelResult) {
        List<Map> maps = null;
        try {
            maps = JSONUtil.parseArray(logoModelResult).toList(Map.class);
        } catch (Exception ex) {
            logger.error("{},json解析出错，请检查格式：logoModelResult = {},error:{}", TraceUtils.getTrace(), logoModelResult, ex.getMessage(), ex);
            throw new ParseException("json解析出错，请检查格式：logoModelResult = " + logoModelResult);
        }
        return maps;
    }


    /**
     * 是否 命中logo && 分数满足阈值
     *
     * @param filters
     * @param imageLogoScore
     * @return true：命中   false：未命中
     */
    public boolean isMatchLogoAndScore(List<FilterConditionDO> filters, Map imageLogoScore) {
        if (filters == null || filters.size() == 0) {
            return false;
        }
        FilterConditionDO logoNameCondition = filters.get(0);
        FilterConditionDO logoScoreCondition = filters.get(1);

        boolean matchLogoName = this.isMatch(logoNameCondition, imageLogoScore, "logoName");
        boolean matchLogoScore = this.isMatch(logoScoreCondition, imageLogoScore, "score");

        if (matchLogoName && matchLogoScore) {
            if (logger.isDebugEnabled()) {
                logger.debug("命中此条规则，图片分析结果为{}，规则条件为[{} {} {}，{} {} {}]", imageLogoScore.toString(),
                        logoNameCondition.getLeftPropertyName(), logoNameCondition.getOperator(), logoNameCondition.getRightValue(),
                        logoScoreCondition.getLeftPropertyName(), logoScoreCondition.getOperator(), logoScoreCondition.getRightValue());
            }
            return true;
        }
        return false;
    }

    /**
     * 是否匹配规则
     *
     * @param filterConditionDO
     * @param imageLogoScore
     * @param field             logo
     * @return
     */
    private boolean isMatch(FilterConditionDO filterConditionDO, Map imageLogoScore, String field) {
        if (filterConditionDO == null) {
            return false;
        }
        String type = filterConditionDO.getRightValueType();
        String operator = filterConditionDO.getOperator();
        String rightValue = filterConditionDO.getRightValue();
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(operator) || StringUtils.isEmpty(rightValue)) {
            return false;
        }
        boolean match = CompareUtils.compare(imageLogoScore.get(field).toString(), rightValue, type, operator);
        return match;
    }

    /**
     * 解析规则信息
     *
     * @param params 规则json
     *               二维数组的json，每一组有2个元素，一个logoName相关，一个score相关
     *               eg:[[{"field":"logoName","operator":"include","value":"lv"},{"field":"score","operator":">","value":500}],[{"field":"logoName","operator":"include","value":"chanel"},{"field":"score","operator":">","value":40}]]
     *               eg:[[{\\\\\\\"field\\\\\\\":\\\\\\\"logoName\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"include\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"lv\\\\\\\"},{\\\\\\\"field\\\\\\\":\\\\\\\"score\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"==\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"9.2234\\\\\\\"}],[{\\\\\\\"field\\\\\\\":\\\\\\\"logoName\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"include\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"chanel\\\\\\\"},{\\\\\\\"field\\\\\\\":\\\\\\\"score\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"==\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"4.2134\\\\\\\"}]]
     * @return
     */
    public List<List<FilterConditionDO>> parseParams(String params) {
        params = params.replace("\\\\\"", "\"").replace("\\", "");
        List<JSONArray> jsonArrays = JSONUtil.parseArray(params).toList(JSONArray.class);
        List<List<FilterConditionDO>> filters = new ArrayList<>();
        for (JSONArray jsonArray : jsonArrays) {
            Map<String, Object> logoName = (Map<String, Object>) jsonArray.get(0);
            FilterConditionDO logoNameCondition = new FilterConditionDO();
            logoNameCondition.setLeftPropertyName(logoName.get("field").toString());
            logoNameCondition.setOperator(logoName.get("operator").toString());
            logoNameCondition.setRightValue(logoName.get("value").toString());
            logoNameCondition.setRightValueType("STRING");

            Map<String, Object> logoScore = (Map<String, Object>) jsonArray.get(1);
            FilterConditionDO logoScoreCondition = new FilterConditionDO();
            logoScoreCondition.setLeftPropertyName(logoScore.get("field").toString());
            logoScoreCondition.setOperator(logoScore.get("operator").toString());
            logoScoreCondition.setRightValue(logoScore.get("value").toString());
            logoScoreCondition.setRightValueType("DOUBLE");

            filters.add(Lists.newArrayList(logoNameCondition, logoScoreCondition));
        }
        return filters;
    }


    private DetailCallable buildDetail(List<List<FilterConditionDO>> hitFilters) {
        return () -> {
            ImageDetail detail = new ImageDetail();
            detail.setRuleUuid(this.ruleUuid);
            detail.setConditionUuid(this.conditionUuid);
            detail.setDescription(description);
            List<String> conditions = new ArrayList<>();
            if (!CollectionUtils.isEmpty(hitFilters)) {
                hitFilters.stream().forEach(hitFilter ->{
                    StringBuilder builder = new StringBuilder();
                    FilterConditionDO logoName = hitFilter.get(0);
                    FilterConditionDO logoScore = hitFilter.get(1);
                    String condition = builder.append(logoName.getLeftPropertyName()).append(" ")
                            .append(logoName.getOperator()).append(" ")
                            .append(logoName.getRightValue()).append(",")
                            .append(logoScore.getLeftPropertyName()).append(" ")
                            .append(logoScore.getOperator()).append(" ")
                            .append(logoScore.getRightValue())
                            .toString();
                    conditions.add(condition);
                });
            }
            detail.setHitConditions(conditions);
            return detail;
        };
    }
}
