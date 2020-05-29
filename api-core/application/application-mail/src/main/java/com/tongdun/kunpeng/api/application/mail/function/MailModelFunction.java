package com.tongdun.kunpeng.api.application.mail.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.util.HttpUtils;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.ruledetail.MailModelDetail;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.alibaba.dubbo.common.json.JSONObject;
import com.google.common.collect.Maps;
import com.tongdun.kunpeng.api.application.mail.constant.MailModelTypeEnum;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeoutException;

import static cn.tongdun.kunpeng.api.common.data.ReasonCode.MAIL_PARAM_NOT_FOUND;

/**
 * @author yuanhang
 * @date 05/27/2020
 */
public class MailModelFunction extends AbstractFunction {

    private static Logger logger = LoggerFactory.getLogger(MailModelFunction.class);

    private String typeStr;

    private Set<String> keys;

    private Integer operate;

    private Integer threshold;

    private List<String> mailTypes;

    @Override
    public String getName() {
        return Constant.Function.MAIL_MODEL;
    }

    @Override
    protected void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("mail model function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("codes", param.getName())) {
                typeStr = param.getValue();
            }
            if (StringUtils.equals("key", param.getName())) {
                keys.add(param.getValue());
            }
            if (StringUtils.equals("operate", param.getName())) {
                operate = Integer.valueOf(param.getValue());
            }
            if (StringUtils.equals("threshold", param.getName())) {
                threshold = Integer.valueOf(param.getValue());
            }
        });

        mailTypes = Arrays.asList(typeStr.split(","));
    }

    @Override
    protected FunctionResult run(ExecuteContext executeContext) {

        AbstractFraudContext context = (AbstractFraudContext) executeContext;
        String url = (String) context.getFieldValues().get("mail.model.url");
        String ranUrl = (String) context.getFieldValues().get("mail.model.random.url");
        String mail = getFirstMail(context);
        if (StringUtils.isNotEmpty(mail)) {
            logger.warn("mails empty, not found mail parameters");
            ReasonCodeUtil.add(context, MAIL_PARAM_NOT_FOUND, null);
            return new FunctionResult(false);
        }

        // TODO 先正则匹配校验一边邮箱格式，减少http接口压力 yuanhang
        try {
            long start = System.currentTimeMillis();

            Map<String, Object> params = constructParams(context);

            url = url + mail;
            ranUrl = ranUrl + mail;
            Map<String, Object> resultMap = getAsyncResponse(url, ranUrl, params);

            // 接口返回Exception情况处理
            if (resultMap.get("urlResult") instanceof ReasonCode) {
                ReasonCodeUtil.add(context, (ReasonCode) resultMap.get("urlResult"), "mail_model");
                return new FunctionResult(false);
            } else if (resultMap.get("randResult") instanceof ReasonCode) {
                ReasonCodeUtil.add(context, (ReasonCode) resultMap.get("randResult"), "mail_model");
                return new FunctionResult(false);
            } else {
                // 处理接口返回结果
                Integer simResult = (Integer) resultMap.get("simResult");
                DetailCallable callable = () -> {
                    MailModelDetail detail = new MailModelDetail();
                    detail.setConditionUuid(this.getConditionUuid());
                    detail.setRuleUuid(this.ruleUuid);
                    if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
                        detail.setRanResult((Double) resultMap.get("ranResult"));
                    }
                    detail.setSimResult(simResult);
                    detail.setTime(System.currentTimeMillis() - start);
                    return detail;
                };

                // TODO 确认勾选中多个条件，返回的唯一结果如何定义
                Map<Integer, String> mapping = MailModelTypeEnum.mappingCode(mailTypes);
                boolean randResult = true;
                // 随机率判定
                if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
                    Integer random = (Integer) resultMap.get("ranResult");
                    switch (operate) {
                        case 0:
                            randResult = random > threshold;
                            break;
                        case 1:
                            randResult = random.equals(threshold);
                            break;
                        case 2:
                            randResult = random < threshold;
                            break;
                        default:
                            randResult = false;
                            break;
                    }
                }
                return new FunctionResult(null == mapping.get(simResult) && randResult ? 0 : simResult, callable);
            }
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.MAIL_MODEL_TIMEOUT_ERROR, "mail_model");
            } else {
                ReasonCodeUtil.add(context, ReasonCode.MAIL_MODEL_NOT_AVAILABLE_ERROR, "mail_model");
            }
            logger.error(TraceUtils.getFormatTrace() + "exception raised when request mail model interface, seqId :{}", context.getSeqId());
        }
        return new FunctionResult(false);
    }

    /**
     * 根据参数获取模型接口结果
     *
     * @param url
     * @param params
     * @return
     */
    private Map<String, Object> getAsyncResponse(String url, String ranUrl, Map<String, Object> params) throws Exception {

        Map<String, Object> result = Maps.newHashMap();
        Map<Request, Object> httpResults = Maps.newHashMap();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(params));
        Request request = new Request.Builder().url(url).post(body).build();

        // 规则配置了随机率参数时，添加并行请求
        if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
            Request ranRequest = new Request.Builder().url(ranUrl).post(body).build();
            HttpUtils.postAsyncJson(Arrays.asList(new Request[]{request, ranRequest}), httpResults);
        } else {
            HttpUtils.postAsyncJson(Arrays.asList(new Request[]{request}), httpResults);
        }

        //处理接口返回结果
        httpResults.entrySet().forEach(r -> {
            String key = url.equals(r.getKey().url()) ? "urlResult" : "randResult";
            String resKey = url.equals(r.getKey().url()) ? "sim_result" : "ran_result";
            if (r.getValue() instanceof Response) {
                Response response = (Response) r.getValue();
                JSONObject res = JSON.parseObject(response.body().toString(), JSONObject.class);
                result.put(key, "ok".equals(res.get("status_msg")) ? res.get(resKey) : ReasonCode.MAIL_MODEL_REQUEST_FAILED);
            } else {
                result.put(key, r.getValue() instanceof TimeoutException ? ReasonCode.MAIL_MODEL_TIMEOUT_ERROR : ReasonCode.MAIL_MODEL_NOT_AVAILABLE_ERROR);
            }
        });

        return result;
    }

    private String getFirstMail(AbstractFraudContext context) {
        Iterator iterable = keys.iterator();
        while (iterable.hasNext()) {
            String mail = (String) context.getFieldValues().get(iterable.next());
            if (StringUtils.isNotEmpty(mail)) {
                return mail;
            }
        }
        return null;
    }

    private Map<String, Object> constructParams(AbstractFraudContext context) {
        Map params = Maps.newHashMap();
        // TODO 等待接口文档
        params.put("business", context.getPartnerCode());
        params.put("mail", getFirstMail(context));
        params.put("time_inteval", "");
        params.put("sim_num", "");
        return params;
    }

    public static void main(String[] args) {
        JSONObject temp = JSON.parseObject("{\"sim_result\":0}\"", JSONObject.class);
        System.out.println(temp.get("sim_result"));
    }

}
