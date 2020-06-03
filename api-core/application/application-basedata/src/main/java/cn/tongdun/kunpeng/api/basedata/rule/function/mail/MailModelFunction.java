package cn.tongdun.kunpeng.api.basedata.rule.function.mail;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.util.HttpUtils;
import cn.tongdun.kunpeng.api.basedata.constant.MailModelTypeEnum;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.ruledetail.MailModelDetail;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.alibaba.dubbo.common.json.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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

    private String operate;

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
                operate = param.getValue();
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
        String url = (String) context.getFieldValues().get("mailModelUrl");
        String ranUrl = (String) context.getFieldValues().get("mailModelRandomUrl");
        if (CollectionUtils.isEmpty(mailTypes)) {
            return new FunctionResult(false);
        }

        String mail = getFirstMail(context);
        if (StringUtils.isNotEmpty(mail)) {
            logger.warn("mails empty, not found mail parameters");
            ReasonCodeUtil.add(context, MAIL_PARAM_NOT_FOUND, null);
            return new FunctionResult(false);
        }

        try {

            long start = System.currentTimeMillis();
            Map<String, Object> params = constructParams(context);
            url = url + mail;
            ranUrl = ranUrl + mail;
            Map<String, Object> resultMap = getAsyncResponse(url, ranUrl, params);

            // 接口返回Exception情况处理
            if (ObjectUtils.allNotNull(resultMap.get("simResult")) && resultMap.get("simResult") instanceof ReasonCode) {
                ReasonCodeUtil.add(context, (ReasonCode) resultMap.get("urlResult"), "mail_model");
                return new FunctionResult(false);
            } else if (ObjectUtils.allNotNull(resultMap.get("randResult")) && resultMap.get("randResult") instanceof ReasonCode) {
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

                Map<Integer, String> mapping = MailModelTypeEnum.mappingCode(mailTypes);
                boolean randResult = true;
                // 随机率判定
                if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
                    Integer random = (Integer) resultMap.get("ranResult");
                    switch (operate) {
                        case ">=":
                            randResult = random >= threshold;
                            break;
                        case "=":
                            randResult = random.equals(threshold);
                            break;
                        case ">":
                            randResult = random > threshold;
                            break;
                        default:
                            randResult = false;
                            break;
                    }
                }
                return new FunctionResult(null == mapping.get(simResult) || randResult, callable);
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
     * 根据参数异步获取模型接口结果
     *
     * @param url
     * @param params
     * @return
     */
    private Map<String, Object> getAsyncResponse(String url, String ranUrl, Map<String, Object> params) throws Exception {

        Map<String, Object> result = Maps.newHashMap();
        Map<Request, Object> httpResults = Maps.newHashMap();

        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), JSON.toJSONString(params));

        List<Request> requests = Lists.newArrayList();
        // 规则配置了随机率参数时，添加并行请求
        if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
            Request ranRequest = new Request.Builder().url(ranUrl).post(body).build();
            requests.add(ranRequest);
        } else {
            Request request = new Request.Builder().url(url).post(body).build();
            requests.add(request);
        }
        HttpUtils.postAsyncJson(requests, httpResults);

        //处理接口返回结果
        httpResults.entrySet().forEach(r -> {
            String key = url.equals(r.getKey().url()) ? "simResult" : "randResult";
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

    /**
     * 字典表可能存在多个mail类型字典，取第一个
     * @param context
     * @return
     */
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

    /**
     * 构建接口需要的参数
     * @param context
     * @return
     */
    private Map<String, Object> constructParams(AbstractFraudContext context) {
        Map params = Maps.newHashMap();
        // TODO 等待接口文档
        params.put("business", context.getPartnerCode());
        params.put("mail", getFirstMail(context));
        params.put("time_inteval", "");
        params.put("sim_num", "");
        return params;
    }

}
