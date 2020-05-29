package cn.tongdun.kunpeng.api.basedata.rule.function.mail;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.basedata.constant.MailModelTypeEnum;
import cn.tongdun.kunpeng.api.basedata.util.HttpUtils;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.ruledetail.MailModelDetail;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.alibaba.dubbo.common.json.JSONObject;
import com.google.common.collect.Maps;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cn.tongdun.kunpeng.api.common.data.ReasonCode.MAIL_PARAM_NOT_FOUND;

/**
 * @author yuanhang
 * @date 05/27/2020
 */
public class MailModelFunction extends AbstractFunction {

    private static Logger logger = LoggerFactory.getLogger(MailModelFunction.class);

    private Set<String> mailType;

    private Set<String> keys;

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
                mailType.add(param.getValue());
            }
            if (StringUtils.endsWith("key", param.getName())) {
                keys.add(param.getValue());
            }
        });
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

            // TODO 异常处理
            if ("TIMEOUT".equals(resultMap.get("error"))) {
                ReasonCodeUtil.add(context, ReasonCode.MAIL_MODEL_REQUEST_FAILED, "mail_model");
                return new FunctionResult(false);
            }

            Integer simResult = (Integer) resultMap.get("simResult");

            DetailCallable callable = () -> {
                MailModelDetail detail = new MailModelDetail();
                detail.setConditionUuid(this.getConditionUuid());
                detail.setRuleUuid(this.ruleUuid);
                if (mailType.contains(MailModelTypeEnum.RANDOM.name())) {
                    detail.setRanResult((Double) resultMap.get("ranResult"));
                }
                detail.setSimResult(mailType.contains(simResult) ? simResult : 0);
                detail.setTime(System.currentTimeMillis() - start);

                return detail;
            };
            return new FunctionResult(mailType.contains(simResult), callable);
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
     * 根据参数确认获取方式
     * @param url
     * @param params
     * @return
     */
    private Map<String, Object> getAsyncResponse(String url,String ranUrl, Map<String, Object> params) throws Exception{

        Map<Request, Object> httpResults = Maps.newHashMap();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(params));
        Request request = new Request.Builder().url(url).post(body).build();
        Request ranRequest = new Request.Builder().url(ranUrl).post(body).build();
        HttpUtils.postAsyncJson(Arrays.asList(new Request[]{request, ranRequest}), httpResults);

        httpResults.entrySet().forEach(r -> {
            if (url.equals(r.getKey().url())) {
                if (r.getValue() instanceof Response) {
                    Response response = (Response) r.getValue();
                    JSONObject res = JSON.parseObject(response.body().toString(),JSONObject.class);
                    Integer simResult = (Integer)res.get("sim_result");

                } else {

                }
            } else if (ranUrl.equals(r.getKey().url())){

            }
        });

        return null;
    }

    private String getFirstMail(AbstractFraudContext context) {
        Iterator iterable = keys.iterator();
        while(iterable.hasNext()) {
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
        if (mailType.contains(MailModelTypeEnum.FORMAT_OBEY_NAME_RULE.code())) {

        };
        return params;
    }

}
