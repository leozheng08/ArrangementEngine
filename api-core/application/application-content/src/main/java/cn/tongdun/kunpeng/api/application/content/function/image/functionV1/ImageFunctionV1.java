package cn.tongdun.kunpeng.api.application.content.function.image.functionV1;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.tongdun.kunpeng.api.application.content.constant.ModelResultEnum;
import cn.tongdun.kunpeng.api.application.content.function.image.FilterConditionDO;
import cn.tongdun.kunpeng.api.application.content.function.image.ImageFunction;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.CompareUtils;
import cn.tongdun.kunpeng.api.ruledetail.ImageDetail;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @description: 内容安全-图片识别
 * @author: zhongxiang.wang
 * @date: 2021-02-20 16:30
 */


public class ImageFunctionV1 extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(ImageFunction.class);
    private String conditions;
    private String logicOperator;
    private String model = "image_brand_logo_model_result";
    private static final String paramKeyConditions = "conditions";
    private static final String paramKeyLogicOperator = "logicOperator";
    private static final String paramKeyModelType = "model";


    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        FunctionResult functionResult = new FunctionResult(false, null);
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        //遍历枚举类型，一次访问会传入多类模型数据
        ModelResultEnum[] modelResultEnums = ModelResultEnum.values();
        Map<ModelResultEnum,String> modelResultEnumToLablelAndScoreModelResult = achieveModelResult(context);
        if (hasNullData(modelResultEnumToLablelAndScoreModelResult,conditions, logicOperator)) {
            return functionResult;
        }
        String LablelAndScoreModelResult = getModelResultMatchingConditionModel(modelResultEnumToLablelAndScoreModelResult,model);
        if(LablelAndScoreModelResult==null){
            return functionResult;
        }
        //解析图片logo及分数信息
        List<Map> lablelAndScoreModelResultList = this.parseLogoModelResult(LablelAndScoreModelResult);
        if (org.springframework.util.CollectionUtils.isEmpty(lablelAndScoreModelResultList)) {
            return functionResult;
        }
        List<List<FilterConditionDO>> hitFilters = new ArrayList<>();
        List<List<FilterConditionDO>> conditionList = this.parseCondition(conditions);
        if(isConditionSatisfied(logicOperator, lablelAndScoreModelResultList, conditionList, hitFilters)){
            //未添加命中规则详情
//            hitFilters.addAll(filters);
            functionResult.setResult(true);
            functionResult.setDetailCallable(this.buildDetail(hitFilters));
        }
        else{
            functionResult.setResult(false);
        }
        return functionResult;
    }

    /**
     * 解析图片logo及分数信息
     *
     * @param lablelAndScoreModelResult 图片解析结果json
     *                        eg:[{"score": 0.9977513551712036,"logoName": "chanel"},{"score": 0.9977513551712126,"logoName": "lv"},{"score": 0.3477513551712036,"logoName": "lv"}]
     * @return
     */
    private List<Map> parseLogoModelResult(String lablelAndScoreModelResult) {
        List<Map> maps = null;
        try {
            maps = JSONUtil.parseArray(lablelAndScoreModelResult).toList(Map.class);
        } catch (Exception ex) {
            logger.error("{},json解析出错，请检查格式：logoModelResult = {},error:{}", TraceUtils.getTrace(), lablelAndScoreModelResult, ex.getMessage(), ex);
            throw new ParseException("json解析出错，请检查格式：logoModelResult = " + lablelAndScoreModelResult);
        }
        return maps;
    }
    /**
     * 解析图片logo及分数信息
     *
     * @param modelResultEnumToLablelAndScoreModelResult 模型分类后的数据
     * @param        model 规则模型
     * return     对应规则模型的数据json
     **/

    private String getModelResultMatchingConditionModel(Map<ModelResultEnum, String> modelResultEnumToLablelAndScoreModelResult, String model) {
        for(Map.Entry<ModelResultEnum, String> ele : modelResultEnumToLablelAndScoreModelResult.entrySet()){
            if(ele.getKey().getName().equals(model)){
                return ele.getValue();
            }
        }
        logger.warn("There is no lablelAndScoreModelResult match Condition model!");
        return null;
    }

    /**
     * 将json格式的条件转化为可判断条件数据结构
     *
     * @param params         规则条件
     *                      二维数组的json，每一组有2个元素，一个logoName相关，一个score相关
     *                     eg:[[{"field":"logoName","operator":"include","value":"lv"},{"field":"score","operator":">","value":500}],[{"field":"logoName","operator":"include","value":"chanel"},{"field":"score","operator":">","value":40}]]
     *                     eg:[[{\\\\\\\"field\\\\\\\":\\\\\\\"logoName\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"include\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"lv\\\\\\\"},{\\\\\\\"field\\\\\\\":\\\\\\\"score\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"==\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"9.2234\\\\\\\"}],[{\\\\\\\"field\\\\\\\":\\\\\\\"logoName\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"include\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"chanel\\\\\\\"},{\\\\\\\"field\\\\\\\":\\\\\\\"score\\\\\\\",\\\\\\\"operator\\\\\\\":\\\\\\\"==\\\\\\\",\\\\\\\"value\\\\\\\":\\\\\\\"4.2134\\\\\\\"}]]
     * @return
     */
    private List<List<FilterConditionDO>> parseCondition(String params) {
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

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if(functionDesc==null|| CollectionUtils.isEmpty(functionDesc.getParamList())){
            throw new ParseException(TraceUtils.getFormatTrace() + "ImageFunction parseFunction error:function or ParamList is null");
        }
        List<FunctionParam> functionParamList = functionDesc.getParamList();
        for(FunctionParam functionParam : functionParamList){
            if(functionParam.getName().equals(paramKeyConditions)){
                conditions = functionParam.getValue();
            }
            if(functionParam.getName().equals(paramKeyLogicOperator)){
                logicOperator = functionParam.getValue();
            }
            if(functionParam.getName().equals(paramKeyModelType)){
                model = functionParam.getValue();
            }

        }
    }

    @Override
    public String getName() {
        return Constant.Function.CONTENT_IMAGE;
    }

    /**
     * 校验数据
     *
     * @param modelResultEnumToLablelAndScoreModelResult 图片分析结果
     * @param conditions      条件
     * @param logicOperator   条件组之间的关系 && ||
     * @return
     */
    private boolean hasNullData(Map<ModelResultEnum,String> modelResultEnumToLablelAndScoreModelResult, String conditions, String logicOperator) {
        if (modelResultEnumToLablelAndScoreModelResult.isEmpty()) {
            logger.warn("The lablelAndScoreModelResult doesn't match modelResultEnum!");
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
     * 遍历枚举类型，默认一次访问只会传入一类模型数据, 但使用Map防止多模型数据传入，同一模型多次输入数据会被覆盖，获取模型数据
     *
     * @param context         上下文
     * @return
     */
    public Map<ModelResultEnum,String> achieveModelResult(AbstractFraudContext context){
        Map<ModelResultEnum,String> ModelResultEnumToLablelAndScoreModelResult = new HashMap<>();
        ModelResultEnum[] modelResultEnums = ModelResultEnum.values();
        //遍历枚举类型，一次访问只会传入一类模型数据
        for(ModelResultEnum modelResultEnum: modelResultEnums){
            Object result = context.get(modelResultEnum.getName());
            if(result!=null){
                String lablelAndScoreModelResult = result.toString();
                ModelResultEnumToLablelAndScoreModelResult.put(modelResultEnum,lablelAndScoreModelResult);
            }
        }
        return ModelResultEnumToLablelAndScoreModelResult;
    }

    /**
     * 是否 命中label && 分数满足阈值
     *
     * @param logicOperator  命中&& ||
     * @param lablelAndScoreModelResultList 访问接口上传的对应模型数据
     * @param conditionList       规则列表
     * @param hitFilters 命中过滤的规则列表
     * @return true：条件全部满足下 全部满足 任意满足下 任意满足   false：条件全部满足下 未能全部满足 任意满足下 全都不满足
     */

    public boolean isConditionSatisfied(String logicOperator, List<Map> lablelAndScoreModelResultList, List<List<FilterConditionDO>> conditionList, List<List<FilterConditionDO>> hitFilters){
        int sum =  conditionList.size();
        int cnt =0;
        if(logicOperator.equals("&&")){
            a:
            for(List<FilterConditionDO> condition :  conditionList){
                b:
                for (Map lablelAndScoreModelResult : lablelAndScoreModelResultList) {
                    if(this.isMatchLabelAndScore(condition,lablelAndScoreModelResult)){
                        cnt++;
                        if(cnt==sum){
                            hitFilters.addAll(conditionList);
                            return true;
                        }
                        break b;
                    }
                }
            }
            return false;
        }
        else{
            for(List<FilterConditionDO> condition :  conditionList){
                for (Map lablelAndScoreModelResult : lablelAndScoreModelResultList) {
                    if(this.isMatchLabelAndScore(condition,lablelAndScoreModelResult)){
                        hitFilters.add(condition);
                        return true;
                    }
                }
            }
            return false;
        }


    }
    /**
     * 构建返回结果详情
     *
     * @param hitFilters 命中的规则详情
     * @return
     */

    private DetailCallable buildDetail(List<List<FilterConditionDO>> hitFilters) {
        return () -> {
            ImageDetail detail = new ImageDetail();
            detail.setRuleUuid(this.ruleUuid);
            detail.setConditionUuid(this.conditionUuid);
            detail.setDescription(description);
            List<String> conditions = new ArrayList<>();
            if (!org.springframework.util.CollectionUtils.isEmpty(hitFilters)) {
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


    /**
     * 是否 命中logo && 分数满足阈值
     *
     * @param condition
     * @param lablelAndScoreModelResult
     * @return true：命中   false：未命中
     */
    public boolean isMatchLabelAndScore(List<FilterConditionDO> condition, Map lablelAndScoreModelResult) {
        if (condition == null || condition.size() == 0) {
            return false;
        }
        FilterConditionDO logoNameCondition = condition.get(0);
        FilterConditionDO logoScoreCondition = condition.get(1);


        boolean matchLogoName = this.isMatch(logoNameCondition, lablelAndScoreModelResult, "label");
        boolean matchLogoScore = this.isMatch(logoScoreCondition, lablelAndScoreModelResult, "score");

        if (matchLogoName && matchLogoScore) {
            if (logger.isDebugEnabled()) {
                logger.debug("命中此条规则，图片分析结果为{}，规则条件为[{} {} {}，{} {} {}]", lablelAndScoreModelResult.toString(),
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
}
