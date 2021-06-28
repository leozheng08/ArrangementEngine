package cn.tongdun.kunpeng.api.application.content.function.image.fucntionV2;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.tongdun.kunpeng.api.application.check.step.CamelAndUnderlineConvertUtil;
import cn.tongdun.kunpeng.api.application.content.constant.MathOperatorEnum;
import cn.tongdun.kunpeng.api.application.content.constant.ModelResultEnum;
import cn.tongdun.kunpeng.api.application.content.function.image.FilterConditionDO;
import cn.tongdun.kunpeng.api.application.content.function.image.ImageFunction;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.CompareUtils;
import cn.tongdun.kunpeng.api.ruledetail.ImageDetail;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ImageFunctionV2 extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(ImageFunction.class);
    private String conditions;
    private String logicOperator;
    private static final String paramKeyConditions = "conditions";
    private static final String paramKeyLogicOperator = "logicOperator";
//    private static final String paramKeyModelType = "model";


    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        FunctionResult functionResult = new FunctionResult(false, null);
        AbstractFraudContext context = (AbstractFraudContext) executeContext;
        ModelResultEnum[] modelResultEnums = ModelResultEnum.values();
        //遍历枚举类型，一次访问会传入多类模型数据
        Map<ModelResultEnum,String> modelResult = achieveModelResult(context);
        if (hasNullData(modelResult,conditions, logicOperator)) {
            return functionResult;
        }
        ConditionModel conditionModel = parseCondition(conditions);
        String matchModelResult = matchModel(modelResult,conditionModel.getModel());
        if(matchModelResult==null){
            return functionResult;
        }
        List<Map> matchModelResultList = this.parseLogoModelResult(matchModelResult);
        List<List<FilterConditionDO>> hitFilters = new ArrayList<>();
        if(isConditionSatisfied(logicOperator, matchModelResultList, conditionModel,hitFilters)){
            functionResult.setResult(true);
            functionResult.setDetailCallable(this.buildDetail(hitFilters));
        }
        else{
            functionResult.setResult(false);
        }
        return functionResult;
    }

    private DetailCallable buildDetail(List<List<FilterConditionDO>> hitFilters) {
        return () -> {
            ImageDetail detail = new ImageDetail();
            detail.setRuleUuid(this.ruleUuid);
            detail.setConditionUuid(this.conditionUuid);
            detail.setDescription(description);
            List<String> conditions = new ArrayList<>();
            if (!org.springframework.util.CollectionUtils.isEmpty(hitFilters)) {
                hitFilters.stream().forEach(hitFilter -> {
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
//            if(functionParam.getName().equals(paramKeyModelType)){
//                model = functionParam.getValue();
//            }

        }
    }

    @Override
    public String getName() {
        return null;
    }


    /**
     * 遍历枚举类型，默认一次访问会传入多类模型数据, 但使用Map防止多模型数据传入，同一模型多次输入数据会被覆盖，获取模型数据
     *
     * @param context         上下文
     * @return
     */
    public Map<ModelResultEnum,String> achieveModelResult(AbstractFraudContext context){
        Map<ModelResultEnum,String> ModelResultEnumToLablelAndScoreModelResult = new HashMap<>();
        ModelResultEnum[] modelResultEnums = ModelResultEnum.values();
        //遍历枚举类型，一次访问只会传入一类模型数据
        for(ModelResultEnum modelResultEnum: modelResultEnums){
            Object result = context.get(modelResultEnum.getCamelName())==null?context.get(modelResultEnum.getName()):context.get(modelResultEnum.getCamelName());
            if(result!=null){
                String lablelAndScoreModelResult = result.toString();
                ModelResultEnumToLablelAndScoreModelResult.put(modelResultEnum,lablelAndScoreModelResult);
            }
        }
        return ModelResultEnumToLablelAndScoreModelResult;
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
     * 获取符合规则模型字段的图片分析结果
     *
     * @param modelResult 图片分析结果
     * @return
     */
    private String matchModel(Map<ModelResultEnum, String> modelResult, String model) {
        for(Map.Entry<ModelResultEnum, String> ele : modelResult.entrySet()){
            if(ele.getKey().getName().equals(model)||ele.getKey().getName().equals(CamelAndUnderlineConvertUtil.underline2camel(model))){
                return ele.getValue();
            }
        }
        logger.warn("There is no lablelAndScoreModelResult match Condition model!");
        return null;
    }


    private ConditionModel parseCondition(String params) {
        params = params.replace("\\\\\"", "\"").replace("\\", "");
        List<JSONArray> jsonArrays = JSONUtil.parseArray(params).toList(JSONArray.class);
        ConditionModel conditionModel = new ConditionModel();
        Map<String,List<ConditionGroup>> labelToConditionGroups = new HashMap<>();
        Map<String,List<FilterConditionDTO>> filters = new HashMap<>();
        String model = null;
        for (JSONArray jsonArray : jsonArrays) {
            Map<String, Object> logoName = (Map<String, Object>) jsonArray.get(0);
            FilterConditionDO logoNameCondition = new FilterConditionDO();
            logoNameCondition.setLeftPropertyName(logoName.get("field").toString());
            logoNameCondition.setOperator(logoName.get("operator").toString());
            logoNameCondition.setRightValue(logoName.get("value").toString());
            //通过解析field的值获得model类别
            if(model==null){
                model = logoName.get("value").toString();
            }
            logoNameCondition.setRightValueType("STRING");

            Map<String, Object> logoScore = (Map<String, Object>) jsonArray.get(1);
            FilterConditionDO logoScoreCondition = new FilterConditionDO();
            logoScoreCondition.setLeftPropertyName(logoScore.get("field").toString());
            logoScoreCondition.setOperator(logoScore.get("operator").toString());
            logoScoreCondition.setRightValue(logoScore.get("value").toString());
            logoScoreCondition.setRightValueType("DOUBLE");

            List<FilterConditionDO> filterConditionDOList = Lists.newArrayList(logoNameCondition, logoScoreCondition);
            FilterConditionDTO filterConditionDTO = new FilterConditionDTO(filterConditionDOList);
            List<FilterConditionDTO> filterConditionDTOList = filters.getOrDefault(logoName.get("value").toString(),new ArrayList<>());
            filterConditionDTOList.add(filterConditionDTO);
            filters.put(logoName.get("value").toString(),filterConditionDTOList);
        }
        labelToConditionGroups = constructGroup(filters);
        conditionModel.setModel(model);
        conditionModel.setLabelToConditionGroups( labelToConditionGroups);
        return conditionModel;
    }

    private Map<String, List<ConditionGroup>> constructGroup(Map<String, List<FilterConditionDTO>> filters) {
        Map<String,List<ConditionGroup>> labelToConditionGroups = new HashMap<>();
        for(Map.Entry<String,List<FilterConditionDTO>> filter : filters.entrySet()){
            String label = filter.getKey();
            List<FilterConditionDTO> filterConditionDTOList = filter.getValue();
            List<ConditionGroup> conditionGroupList = combine(filterConditionDTOList);
            labelToConditionGroups.put(label,conditionGroupList);
        }
        return  labelToConditionGroups;
    }

    private List<ConditionGroup> combine(List<FilterConditionDTO> filterConditionDTOList) {
        List<ConditionGroup> res = new ArrayList<>();
        PriorityQueue<FilterConditionDTO> leftSideQueue = new PriorityQueue<FilterConditionDTO>((a,b)->{
            if(a.getValue()<b.getValue()){
                return -1;
            }
            else if(a.getValue()>b.getValue()){
                return 1;
            } else{
                return 0;
            }
        });
        PriorityQueue<FilterConditionDTO> rightSideQueue = new PriorityQueue<>((a,b)->{
            if(a.getValue()<b.getValue()){
                return -1;
            }
            else if(a.getValue()>b.getValue()){
                return 1;
            } else{
                return 0;
            }
        });
        for(FilterConditionDTO filterConditionDTO : filterConditionDTOList){
            if(filterConditionDTO.getMathOperatorEnum().equals(MathOperatorEnum.LESS_EQUAL)||filterConditionDTO.getMathOperatorEnum().equals(MathOperatorEnum.LESS)){
                rightSideQueue.add(filterConditionDTO);
            }
            else{
                leftSideQueue.add(filterConditionDTO);
            }
        }

        // improve!
        while(!leftSideQueue.isEmpty()&&!rightSideQueue.isEmpty()){
            ConditionGroup conditionGroup = new ConditionGroup();
            FilterConditionDTO filterConditionDTO = leftSideQueue.poll();
            if(filterConditionDTO.getMathOperatorEnum().equals(MathOperatorEnum.MORE)||filterConditionDTO.getMathOperatorEnum().equals(MathOperatorEnum.MORE_EQUAL)){
                if(filterConditionDTO.getValue()<=rightSideQueue.peek().getValue()){
                    conditionGroup.setLeftCondition(filterConditionDTO.getFilterConditionDOS());
                    FilterConditionDTO filterConditionDTO1 = rightSideQueue.poll();
                    conditionGroup.setRightCondition(filterConditionDTO1.getFilterConditionDOS());
                    res.add(conditionGroup);
                }
                else{
                    conditionGroup.setLeftCondition(filterConditionDTO.getFilterConditionDOS());
                    res.add(conditionGroup);
                }
            }
            else{
                conditionGroup.setLeftCondition(filterConditionDTO.getFilterConditionDOS());
                res.add(conditionGroup);
            }
        }
        while(!leftSideQueue.isEmpty()){
            ConditionGroup conditionGroup = new ConditionGroup();
            FilterConditionDTO filterConditionDTO = leftSideQueue.poll();
            conditionGroup.setLeftCondition(filterConditionDTO.getFilterConditionDOS());
            res.add(conditionGroup);
        }
        while(!rightSideQueue.isEmpty()){
            ConditionGroup conditionGroup = new ConditionGroup();
            FilterConditionDTO filterConditionDTO = leftSideQueue.poll();
            conditionGroup.setRightCondition(filterConditionDTO.getFilterConditionDOS());
            res.add(conditionGroup);
        }
        return res;

    }

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

    public boolean isConditionSatisfied(String logicOperator, List<Map> matchModelResultList, ConditionModel conditionModel, List<List<FilterConditionDO>> hitFilters){
        Map<String, List<ConditionGroup>> labelToConditionGroups = conditionModel.getLabelToConditionGroups();
        if(StringUtils.equalsIgnoreCase(logicOperator,"&&")){
            int times = calcul(labelToConditionGroups);
            Integer cnt = 0;
            a:
            for(Map.Entry<String, List<ConditionGroup>> labelToConditionGroup: labelToConditionGroups.entrySet()){
               b:
               for(ConditionGroup conditionGroup : labelToConditionGroup.getValue()){
                   if(containsMatchModelResult(conditionGroup,matchModelResultList,"&&")){
                       cnt++;
                       if(cnt==times){
                           hitFilters.addAll(ConvertConditionGroupsToFilterConditions(labelToConditionGroups));
                           return true;
                       }
                       break b;
                   }
               }
            }
            return false;
        }

            a:
            for(Map.Entry<String, List<ConditionGroup>> labelToConditionGroup: labelToConditionGroups.entrySet()){
                b:
                for(ConditionGroup conditionGroup : labelToConditionGroup.getValue()){
                    if(containsMatchModelResult(conditionGroup,matchModelResultList,"||")){
                            hitFilters.add(ConvertConditionGroupToFilterConditions(conditionGroup));
                            return true;
                        }
                    }
            }
            return false;


    }

    private List<FilterConditionDO> ConvertConditionGroupToFilterConditions(ConditionGroup conditionGroup) {
        List<FilterConditionDO> res = new ArrayList<>();
        if(conditionGroup.getLeftCondition()!=null){
            res.add(conditionGroup.getLeftCondition().get(0));
            res.add(conditionGroup.getLeftCondition().get(1));
        }
        if(conditionGroup.getRightCondition()!=null){
            res.add(conditionGroup.getRightCondition().get(0));
            res.add(conditionGroup.getRightCondition().get(1));
        }
        return res;
    }

    private List<List<FilterConditionDO>> ConvertConditionGroupsToFilterConditions(Map<String, List<ConditionGroup>> labelToConditionGroups) {
        List<List<FilterConditionDO>> res = new ArrayList<>();
        for(Map.Entry<String, List<ConditionGroup>> labelToConditionGroup : labelToConditionGroups.entrySet()){
            for(ConditionGroup conditionGroup : labelToConditionGroup.getValue()){
                if(conditionGroup.getLeftCondition()!=null){
                    res.add(conditionGroup.getLeftCondition());
                }
                if(conditionGroup.getRightCondition()!=null){
                    res.add(conditionGroup.getRightCondition());
                }
            }
        }
        return res;
    }


    private int calcul(Map<String, List<ConditionGroup>> labelToConditionGroups) {
        Integer cnt = 0;
        for(Map.Entry<String, List<ConditionGroup>> labelToConditionGroup : labelToConditionGroups.entrySet()){
            cnt += labelToConditionGroup.getValue().size();
        }
        return cnt;
    }

    private boolean containsMatchModelResult(ConditionGroup conditionGroup, List<Map> lablelAndScoreModelResultList, String logicOperator) {
        if(logicOperator.equals("&&")){
            for (Map lablelAndScoreModelResult : lablelAndScoreModelResultList) {
                if(conditionGroup.getLeftCondition()!=null&&conditionGroup.getRightCondition()!=null){
                    if (this.isMatchLogoAndScore(conditionGroup.getLeftCondition(), lablelAndScoreModelResult)&&this.isMatchLogoAndScore(conditionGroup.getLeftCondition(), lablelAndScoreModelResult)){
                        return true;
                    }
                }
                else if(conditionGroup.getLeftCondition()!=null){
                    if (this.isMatchLogoAndScore(conditionGroup.getLeftCondition(),lablelAndScoreModelResult)){
                        return true;
                    }
                }
                if (this.isMatchLogoAndScore(conditionGroup.getRightCondition(),lablelAndScoreModelResult)){
                    return true;
                }
            }
            return false;
        }
        else{
            for (Map lablelAndScoreModelResult : lablelAndScoreModelResultList){
                if(conditionGroup.getLeftCondition()!=null&&this.isMatchLogoAndScore(conditionGroup.getLeftCondition(),lablelAndScoreModelResult)){
                    return true;
                }
                if(conditionGroup.getRightCondition()!=null&&this.isMatchLogoAndScore(conditionGroup.getRightCondition(),lablelAndScoreModelResult)){
                    return true;
                }
            }
            return false;
        }

    }


    public boolean isMatchLogoAndScore(List<FilterConditionDO> filters, Map imageLogoScore) {
        if (filters == null || filters.size() == 0) {
            return false;
        }
        FilterConditionDO logoNameCondition = filters.get(0);
        FilterConditionDO logoScoreCondition = filters.get(1);


        boolean matchLogoName = this.isMatch(logoNameCondition, imageLogoScore, logoNameCondition.getLeftPropertyName());
        boolean matchLogoScore = this.isMatch(logoScoreCondition, imageLogoScore, logoScoreCondition.getLeftPropertyName());

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
