package cn.tongdun.kunpeng.api.application.keyword.step;

import cn.fraudmetrix.nlas.dubbo.common.bean.WordModel;
import cn.fraudmetrix.nlas.dubbo.common.bean.WordResultModel;
import cn.fraudmetrix.nlas.dubbo.service.word.WordSearchDubboService;
import cn.tongdun.kunpeng.api.application.keyword.constant.KeywordConstant;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.cache.BatchRemoteCallDataCache;
import cn.tongdun.kunpeng.api.engine.convertor.batch.keyword.KeywordBatchRemoteCallData;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @description: 用缓存中的规则数据，调用dubbo批量接口，获取匹配结果，存入上下文中
 * @author: zhongxiang.wang
 * @date: 2021-01-28 13:53
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.RULE_DATA)
public class KeywordStep implements IRiskStep {

    private static final Logger logger = LoggerFactory.getLogger(KeywordStep.class);

    @Autowired
    private BatchRemoteCallDataCache batchRemoteCallDataCache;
    @Autowired
    private WordSearchDubboService wordSearchDubboService;

    /**
     * 用缓存中的规则数据，调用dubbo批量接口，获取匹配结果，存入上下文中
     *
     * @param context
     * @param response
     * @param request
     * @return
     */
    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String policyUuid = context.getPolicyUuid();
        //1.缓存数据获取及校验
        Map<String, List<Object>> batchDataObjectsMap = batchRemoteCallDataCache.get(policyUuid);
        List<Object> batchDataObjects = null;
        if (CollectionUtils.isEmpty(batchDataObjectsMap) ||
                CollectionUtils.isEmpty(batchDataObjects = batchDataObjectsMap.get(Constant.Function.KEYWORD_WORDLIST))) {
            if (logger.isDebugEnabled()) {
                logger.debug("从缓存中未查询到关键词相关批量远程调用数据，policyUuid = {}", policyUuid);
            }
            return true;
        }

        List<WordModel> wordModels = new ArrayList<>();
        for (Object obj : batchDataObjects) {
            KeywordBatchRemoteCallData callData = (KeywordBatchRemoteCallData) obj;
            String calcField = callData.getCalcField();
            String definitionList = callData.getDefinitionList();
            if (StringUtils.isEmpty(calcField) || StringUtils.isEmpty(definitionList) || null == context.get(calcField)) {
                continue;
            }
            //2.组装远程调用参数
            String dimValuesStr = context.get(calcField).toString();
            List<String> dimValues = dimValuesStr.contains(KeywordConstant.SPLIT) ?
                    Arrays.asList(dimValuesStr.split(KeywordConstant.SPLIT)) : Arrays.asList(dimValuesStr);
            for (String dimValue : dimValues) {
                this.handleWordModel(wordModels, dimValue, definitionList);
            }
        }
        if (CollectionUtils.isEmpty(wordModels)) {
            if (logger.isDebugEnabled()) {
                logger.debug("未查询到缓存中有关键词规则数据配置，policyUuid = {}", policyUuid);
            }
            return true;
        }

        //3.远程调用
        List<WordResultModel> wordResultModels = null;
        try {
            wordResultModels = wordSearchDubboService.searchList(context.getSeqId(), context.getPartnerCode(), context.getAppName(), wordModels);
        } catch (Exception ex) {
            logger.error("关键词规则dubbo远程批量调用出错,{}", ex.getMessage(), ex);
            logger.error(TraceUtils.getTrace() + "关键词规则dubbo远程批量调用出错,policyUuid = {},{}", policyUuid, ex.getMessage(), ex);
            if (ReasonCodeUtil.isTimeout(ex)) {
                ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_TIMEOUT, "nlas");
                logger.error(TraceUtils.getFormatTrace() + "WordSearchDubboService.searchList error:" + JSON.toJSONString(wordResultModels), ex);
            } else {
                ReasonCodeUtil.add(context, ReasonCode.NLAS_CALL_ERROR, "nlas");
                logger.error(TraceUtils.getFormatTrace() + "WordSearchDubboService.searchList error:" + JSON.toJSONString(wordResultModels), ex);
            }
        }

        //4.远程调用结果转换，存入上下文中
        if (CollectionUtils.isEmpty(wordResultModels)) {
            if (logger.isDebugEnabled()) {
                logger.debug("关键词规则dubbo远程批量调用返回结果为空，policyUuid = {}", policyUuid);
            }
        } else {
            List<Object> result = new ArrayList<>();
            wordResultModels.stream().forEach(wordResultModel -> result.add(wordResultModel));
            context.setKeywordResultModels(result);
        }
        return true;
    }

    /**
     * 准备dubbo调用参数
     *
     * @param wordModels
     * @param dimValue
     * @param definitionList
     */
    private void handleWordModel(List<WordModel> wordModels, String dimValue, String definitionList) {
        WordModel existWordModel = null;
        for (WordModel wordModel : wordModels) {
            if (dimValue.equals(wordModel.getValue())) {
                existWordModel = wordModel;
            }
        }
        if (null == existWordModel) {
            WordModel wordModel = new WordModel();
            wordModel.setValue(dimValue);
            wordModel.setWordListCodes(Lists.newArrayList(definitionList));
            wordModels.add(wordModel);
        } else {
            List<String> wordListCodes = existWordModel.getWordListCodes();
            if (!CollectionUtils.isEmpty(wordListCodes)) {
                if (!wordListCodes.contains(definitionList)) {
                    existWordModel.getWordListCodes().add(definitionList);
                }
            }
        }
    }
}
