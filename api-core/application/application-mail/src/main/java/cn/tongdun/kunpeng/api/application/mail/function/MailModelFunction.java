package cn.tongdun.kunpeng.api.application.mail.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.application.mail.constant.MailModelResult;
import cn.tongdun.kunpeng.api.application.mail.constant.MailModelTypeEnum;
import cn.tongdun.kunpeng.api.application.util.HttpUtils;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.DateUtil;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.ruledetail.MailModelDetail;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;

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

    private Set<String> keys = Sets.newHashSet();

    private String operate;

    private Integer threshold;

    private List<String> mailTypes;

    private String timeInterval = "2";

    private String simResult = "10";

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
            if (StringUtils.equals("timeInterval", param.getName())) {
                timeInterval = param.getValue();
            }
            if (StringUtils.equals("simNum", param.getName())) {
                simResult = param.getValue();
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
        if (StringUtils.isEmpty(mail)) {
            logger.warn("mails empty, not found mail parameters");
            ReasonCodeUtil.add(context, MAIL_PARAM_NOT_FOUND, null);
            return new FunctionResult(false);
        }

        try {

            long start = System.currentTimeMillis();
            Object result = getModelResponse(url, ranUrl, mail, context);

            if (null == result) {
                ReasonCodeUtil.add(context, ReasonCode.MAIL_MODEL_OTHER_EXCEPTION, "mail_model");
                return new FunctionResult(false);
            }
            // 接口返回Exception情况处理
            if (result instanceof ReasonCode) {
                ReasonCodeUtil.add(context, (ReasonCode) result, "mail_model");
                return new FunctionResult(false);
            } else {
                // 处理接口返回结果
                MailModelResult mailModelResult = (MailModelResult) result;
                DetailCallable callable = () -> {
                    MailModelDetail detail = new MailModelDetail();
                    detail.setConditionUuid(this.getConditionUuid());
                    detail.setRuleUuid(this.ruleUuid);
                    if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
                        detail.setRanResult(mailModelResult.getRanResult());
                    }
                    detail.setSimResult(null == mailModelResult.getResult() ? 0 : (Integer)mailModelResult.getResult());
                    detail.setTime(System.currentTimeMillis() - start);
                    return detail;
                };

                Map<Integer, String> mapping = MailModelTypeEnum.mappingCode(mailTypes);
                boolean randResult = true;
                // 随机率判定
                if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
                    Double random = mailModelResult.getRanResult();
                    if (null != random) {
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
                }
                return new FunctionResult(null == mapping.get(mailModelResult.getResult()) || randResult, callable);
            }
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(context, ReasonCode.MAIL_MODEL_TIMEOUT_ERROR, "mail_model");
            } else {
                ReasonCodeUtil.add(context, ReasonCode.MAIL_MODEL_NOT_AVAILABLE_ERROR, "mail_model");
            }
            logger.error(TraceUtils.getFormatTrace() + "exception raised when request mail model interface :{}", e);
        }
        return new FunctionResult(false);
    }

    /**
     * 根据参数异步获取模型接口结果
     *
     * @param url
     * @param ranUrl
     * @return
     */
    private Object getModelResponse(String url, String ranUrl, String mail, AbstractFraudContext context) {

        Map<Request, Object> httpResults = Maps.newHashMap();

        MailModelResult result = new MailModelResult();
        try {

            // 表单格式请求
            RequestBody body = new FormBody.Builder()
                    .add("business", context.getPartnerCode())
                    .add("mail", mail)
                    .add("time_inteval", timeInterval)
                    .add("sim_num", simResult)
                    .add("event_time", DateUtil.parseNowDatetimeFormat())
                    .build();

            List<Request> requests = Lists.newArrayList();
            // 规则配置了随机率参数时，添加并行请求

            if (mailTypes.contains(MailModelTypeEnum.RANDOM.code())) {
                Request ranRequest = new Request.Builder().url(ranUrl).post(body).build();
                requests.add(ranRequest);
            }

            boolean flag = (mailTypes.contains(MailModelTypeEnum.RANDOM.code()) && mailTypes.size() > 1)
                    || mailTypes.size() > 0;
            if (flag) {
                Request request = new Request.Builder().url(url).post(body).build();
                requests.add(request);
            }
            HttpUtils.postAsyncJson(requests, httpResults);

            //处理接口返回结果
            Iterator<Map.Entry<Request, Object>> var0 = httpResults.entrySet().iterator();
            while (var0.hasNext()) {
                Map.Entry<Request, Object> entry = var0.next();
                // 邮箱模型服务
                String response = (String) entry.getValue();
                if (url.equals(entry.getKey().url().toString())) {
                    if (entry.getValue() instanceof String) {
                        MailModelResult simResult = JSON.parseObject(response, MailModelResult.class);
                        result.setStatus_code(simResult.getStatus_code());
                        result.setStatus_msg(result.getStatus_msg());
                        switch (simResult.getStatus_code()) {
                            case -1:
                                return ReasonCode.MAIL_MODEL_REQUEST_FAILED;
                            case 408:
                                return ReasonCode.MAIL_MODEL_TIMEOUT_ERROR;

                        }
                    } else {
                        return entry.getValue() instanceof TimeoutException ? ReasonCode.MAIL_MODEL_TIMEOUT_ERROR : ReasonCode.MAIL_MODEL_NOT_AVAILABLE_ERROR;
                    }
                    // 随机率服务
                } else {
                    if (entry.getValue() instanceof String) {
                        MailModelResult randResult = JSON.parseObject(response, MailModelResult.class);
                        switch (randResult.getStatus_code()) {
                            case -1:
                                return ReasonCode.MAIL_MODEL_RANDOM_REQUEST_FAILED;
                            case 408:
                                return ReasonCode.MAIL_MODEL_RANDOM_TIMEOUT_ERROR;

                        }
                        result.setRanResult(Double.parseDouble(randResult.getResult().toString()) * 100);
                    } else {
                        return entry.getValue() instanceof TimeoutException ? ReasonCode.MAIL_MODEL_RANDOM_TIMEOUT_ERROR : ReasonCode.MAIL_MODEL_RANDOM_NOT_AVAILABLE_ERROR;
                    }
                }
            }
            return result;
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "http request raise exception :{}", e);
            return ReasonCode.MAIL_MODEL_OTHER_EXCEPTION;
        }
    }

    /**
     * 字典表可能存在多个mail类型字典，取第一个
     *
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

}
