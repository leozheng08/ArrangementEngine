package cn.tongdun.kunpeng.api.application.keyword.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.fraudmetrix.nlas.core.search.SearchedWord;
import cn.fraudmetrix.nlas.dubbo.common.bean.SearchResultBean;
import cn.fraudmetrix.nlas.dubbo.common.bean.WordResultModel;
import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.keyword.constant.KeywordConstant;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.ruledetail.KeywordDetail;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.tongdun.kunpeng.api.application.keyword.constant.KeywordConstant.*;

/**
 * @description: 关键词
 * @author: zhongxiang.wang
 * @date: 2021-01-28 11:57
 */
public class KeywordFunction extends AbstractFunction {

    private static final Logger logger = LoggerFactory.getLogger(KeywordFunction.class);

    // [{"name":"calcField","type":"string","value":"partnerCode"},{"name":"definitionList","type":"string","value":"sdgjcb"}]

    /**
     * 匹配字段的值 eg:partnerCode
     */
    private String calcField;
    /**
     * 关键词列表的code eg:shgjcb
     */
    private String definitionList;
    /**
     * 匹配模式 eg:ATTR
     */
    private String matchMode;
    /**
     * 满足条件
     */
    public String attrInfo;

    /**
     * 关键词匹配核心逻辑
     * 对调用方传参和上下文中存储的匹配结果进行匹配处理，返回最终结果
     *
     * @param executeContext
     * @return
     */
    @Override
    protected FunctionResult run(ExecuteContext executeContext) {
        if (null == ((AbstractFraudContext) executeContext).get(calcField)){
            return new FunctionResult(false, null);
        }
        String dimValuesStr = ((AbstractFraudContext) executeContext).get(calcField).toString();
        List<String> dimValues = dimValuesStr.contains(KeywordConstant.SPLIT) ? Arrays.asList(dimValuesStr.split(KeywordConstant.SPLIT)) : Arrays.asList(dimValuesStr);
        if (hasNullData(calcField, definitionList, matchMode, dimValues)) {
            return new FunctionResult(false, null);
        }
        List<Object> keywordResultModels = ((AbstractFraudContext) executeContext).getKeywordResultModels();
        if (CollectionUtils.isEmpty(keywordResultModels)) {
            return new FunctionResult(false, null);
        } else {
            return this.matchResult((FraudContext) executeContext, keywordResultModels, dimValues, definitionList, matchMode);
        }
    }

    /**
     * 解析参数
     *
     * @param functionDesc
     */
    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException(TraceUtils.getFormatTrace() + "KeywordFunction parseFunction error:function or ParamList is null");
        }
        functionDesc.getParamList().stream().forEach(param -> {
            if (KeywordConstant.PARAM_KEY_CALC_FIELD.equals(param.getName())) {
                calcField = param.getValue();
            }
            if (KeywordConstant.PARAM_KEY_DEFINITION_LIST.equals(param.getName())) {
                definitionList = param.getValue();
            }
            if (KeywordConstant.PARAM_KEY_MATCH_MODE.equals(param.getName())) {
                matchMode = param.getValue();
            }
            if (KeywordConstant.PARAM_KEY_ATTR_INFO.equals(param.getName())) {
                attrInfo = param.getValue();
            }
        });
    }

    @Override
    public String getName() {
        return Constant.Function.KEYWORD_WORDLIST;
    }

    /**
     * 结果匹配处理
     *
     * @param context
     * @param keywordResultModels 在{@link cn.tongdun.kunpeng.api.application.keyword.step.KeywordStep#invoke(AbstractFraudContext, IRiskResponse, RiskRequest)}
     *                            中处理存入的远程调用结果
     * @param dimValues
     * @param wordListCode
     * @param matchMode
     * @return
     */
    private FunctionResult matchResult(FraudContext context, List<Object> keywordResultModels, List<String> dimValues, String wordListCode, String matchMode) {
        //匹配上的结果
        List<Map<String, Object>> matchData = null;
        //遍历每个dim value，看是否匹配
        for (int i = 0; i < dimValues.size(); i++) {
            String dimValue = dimValues.get(i);
            if (StringUtils.isEmpty(dimValue)) {
                continue;
            }
            SearchResultBean searchResultBean = this.getSearchResultBean(keywordResultModels, dimValue, wordListCode);
            String replaceText = null == searchResultBean ? null : searchResultBean.getReplacedText();

            //匹配结果为空
            if (null == searchResultBean || CollectionUtils.isEmpty(searchResultBean.getWords())) {
                matchData = null;
            } else {
                matchData = Lists.newArrayListWithCapacity(searchResultBean.getWords().size());
                //属性匹配
                if (MATCH_MODE_ATTR.equals(matchMode)) {
                    this.handleAttr(context, searchResultBean.getWords(), matchData, attrInfo);
                } else {
                    //模糊匹配
                    for (SearchedWord word : searchResultBean.getWords()) {
                        Map<String, Object> temp = Maps.newHashMapWithExpectedSize(2);
                        temp.put(KEY_KEYWORD, word.getWord());
                        if (null != word.getWordAttributes() && null != word.getWordAttributes().getValues()) {
                            temp.put(PARAM_KEY_ATTR_INFO, word.getWordAttributes().getValues());
                            temp.put(KEY_REPLACE_TEXT, replaceText);
                        }
                        matchData.add(temp);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(matchData)) {
            return new FunctionResult(false, null);
        } else {
            return new FunctionResult(true, this.buildDetail(matchData));
        }
    }

    /**
     * 属性匹配
     *
     * @param context
     * @param words
     * @param data
     * @param attrInfo
     */
    private void handleAttr(FraudContext context, List<SearchedWord> words, List<Map<String, Object>> data, String attrInfo) {
        if (StringUtils.isEmpty(attrInfo)) {
            return;
        }
        String[] array = attrInfo.split("\\|");
        List<AttrExp> attrExps = new ArrayList<>(array.length);
        for (String str : array) {
            JSONObject jsonObject = JSONObject.parseObject(str, JSONObject.class);
            AttrExp attrExp = new AttrExp();
            attrExp.setAndOr(jsonObject.getString("andOr"));
            attrExp.setLeft(jsonObject.getString("left"));
            attrExp.setOperate(jsonObject.getString("operate"));
            attrExp.setRight(jsonObject.getString("right"));
            attrExp.setRightType(jsonObject.getString("rightType"));
            attrExps.add(attrExp);
        }
        for (SearchedWord word : words) {
            Map<String, Object> temp = Maps.newHashMapWithExpectedSize(3);
            temp.put(KEY_KEYWORD, word.getWord());
            if (null != word.getWordAttributes() || !CollectionUtils.isEmpty(word.getWordAttributes().getValues())) {
                boolean flag = NlasAttrExpUtil.calc(context, word.getWordAttributes().getValues(), attrExps);
                if (flag) {
                    temp.put(PARAM_KEY_ATTR_INFO, word.getWordAttributes().getValues());
                }
            }
            if (!temp.isEmpty()) {
                data.add(temp);
            }
        }
    }

    /**
     * 筛选结果
     *
     * @param result
     * @param dimValue
     * @param wordListCode
     * @return
     */
    private SearchResultBean getSearchResultBean(List<Object> result, String dimValue, String wordListCode) {
        for (Object one : result) {
            WordResultModel WordResultModel = (WordResultModel) one;
            if (dimValue.equals(WordResultModel.getValue())) {
                Map<String, SearchResultBean> data = WordResultModel.getMap();
                if (data != null) {
                    return data.get(wordListCode);
                }
            }
        }
        return null;
    }

    /**
     * 构造详情
     *
     * @return
     */
    private DetailCallable buildDetail(List<Map<String, Object>> data) {
        return () -> {
            KeywordDetail detail = new KeywordDetail();
            detail.setRuleUuid(this.ruleUuid);
            detail.setConditionUuid(this.conditionUuid);
            detail.setDescription(description);
            detail.setData(data);
            return detail;
        };
    }

    /**
     * check null data
     *
     * @param calcField    匹配字段
     * @param wordListCode 关键词列表code
     * @param matchMode
     * @param dimValues
     * @return
     */
    private boolean hasNullData(String calcField, String wordListCode, String matchMode, List<String> dimValues) {
        if (StringUtils.isEmpty(calcField)) {
            logger.warn("calcField is null");
            return true;
        }
        if (StringUtils.isEmpty(wordListCode)) {
            logger.warn("wordListCode is null");
            return true;
        }
        if (StringUtils.isEmpty(matchMode)) {
            logger.warn("matchMode is null");
            return true;
        }
        if (CollectionUtils.isEmpty(dimValues)) {
            logger.warn("dimValue list is null");
            return true;
        }
        return false;
    }

}
