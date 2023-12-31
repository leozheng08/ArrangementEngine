package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.check.step.assign.*;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.api.common.util.DateUtil;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDataType;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static cn.tongdun.kunpeng.client.data.RiskRequest.fieldGetMethodMap;

/**
 * 设置上下文中的字段值
 *
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 1100)
public class AssignFieldValueStep implements IRiskStep {

    @Value("${kunpeng.partner.time:globalegrow,pagsmile,derica}")
    private String KUNPENG_PARTNER_TIME = "globalegrow,pagsmile,derica";

    private Logger logger = LoggerFactory.getLogger(AssignFieldValueStep.class);

    private static final HashSet<String> excludeFieldName = new HashSet<String>() {

        private static final long serialVersionUID = 1L;

        {
            add("seq_id");
            add("sequenceId");
            add("appName");
            add("appType");
            add("eventType");
            add("firstIndustryType");
            add("secondIndustryType");
            add("policy_uuid");
        }
    };

    private static final Map<String, Assign> dataTypeAssignMap = new HashMap<>(7);

    static {
        dataTypeAssignMap.put(FieldDataType.OBJECT.name(), new ObjectAssign());
        dataTypeAssignMap.put(FieldDataType.ARRAY.name(), new ArrayAssign());
        dataTypeAssignMap.put(FieldDataType.BOOLEAN.name(), new BooleanAssign());
        dataTypeAssignMap.put(FieldDataType.DATETIME.name(), new DatetimeAssign());
        dataTypeAssignMap.put(FieldDataType.DOUBLE.name(), new DoubleAssign());
        dataTypeAssignMap.put(FieldDataType.INT.name(), new IntAssign());
        dataTypeAssignMap.put(FieldDataType.STRING.name(), new StringAssign());
        dataTypeAssignMap.put(FieldDataType.LONG.name(), new LongAssign());
    }

    @Autowired
    private PartnerCache partnerCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        Partner partner = partnerCache.get(context.getPartnerCode());

        /*************根据字段的定义，将请求参数设置到上下文中 start**********************/
        setFraudContext(context, request);
        /*************根据字段的定义，将请求参数设置到上下文中 end**********************/


        /*************决策引擎除字段以外其他参数的获取**********************/
        //service-type=creditcloud时决策接口直接返回详情
        String serviceType = request.getServiceType();
        if (StringUtils.isBlank(serviceType)) {
            serviceType = "professional";
        }
        context.setServiceType(serviceType);

        //判断是否测试数据
        context.setTestFlag(request.isTestFlag());

        // 设置合作方的行业信息
        if (partner != null) {
            context.set("firstIndustryType", partner.getIndustryType());
            context.set("secondIndustryType", partner.getSecondIndustryType());
        }

        //调用async,如果async=true，则没有800ms规则引擎超时
        context.setAsync(request.isAsync());

        //如果客户有传事件发生时间，为客户传的为准
        try {
            Object eventOccurTime = request.getFieldValues().get("eventOccurTime");
            if (eventOccurTime != null && StringUtils.isNotEmpty(String.valueOf(eventOccurTime)) && eventOccurTime instanceof String) {
                Date date = DateUtil.parseDateTimeUTC(eventOccurTime.toString());
                if (KUNPENG_PARTNER_TIME.contains(context.getPartnerCode())) {
                    date = DateUtil.parseDateTimeBeiJing(eventOccurTime.toString());
                }
                context.setEventOccurTime(date);
            } else if (eventOccurTime != null && StringUtils.isNotEmpty(String.valueOf(eventOccurTime)) && eventOccurTime instanceof Date) {
                context.setEventOccurTime((Date) eventOccurTime);
            }
        } catch (Exception e) {
            logger.error("parse eventOccurTime raise ex:{}", e);
        }
        return true;
    }

//    public static void main(String[] args) {
//        try {
//            Date date = DateUtil.parseDateTimeBeiJing("2021-5-18 10:22:33");
//            Date date1 = DateUtil.parseDateTimeUTC("2021-5-18 10:22:33");
//            System.out.println("--"+date.getTime()+"--"+date1.getTime());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }


    public void setFraudContext(AbstractFraudContext ctx, RiskRequest request) {
        if (null == ctx || null == request || request.getFieldValues().isEmpty()) {
            return;
        }

        for (Map.Entry<String, Object> entry : request.getFieldValues().entrySet()) {
            if (null == entry.getValue()) {
                continue;
            }
            IFieldDefinition fieldDefinition = ctx.getFieldDefinition(entry.getKey());
            if (null == fieldDefinition) {
                String stardandCode = CamelAndUnderlineConvertUtil.underline2camel(entry.getKey());
                if (null == stardandCode) {
                    continue;
                }
                fieldDefinition = ctx.getFieldDefinition(stardandCode);
            }
            if (null == fieldDefinition) {
                continue;
            }

            putValueByFieldDefinition(ctx.getFieldValues(), fieldDefinition, entry.getValue());
        }

        //解决AbstracFraundContext类中get方法反射抛出异常错误（AbstractFraudContext异常位置4,object is not an instance of declaring class）
        for (String key : fieldGetMethodMap.keySet()) {
            Object value = request.get(key);
            if (value == null) {
                continue;
            }
            IFieldDefinition fieldDefinition = ctx.getFieldDefinition(key);
            if (null == fieldDefinition) {
                String stardandCode = CamelAndUnderlineConvertUtil.underline2camel(key);
                if (null == stardandCode) {
                    continue;
                }
                fieldDefinition = ctx.getFieldDefinition(stardandCode);
            }
            if (null == fieldDefinition) {
                continue;
            }

            putValueByFieldDefinition(ctx.getFieldValues(), fieldDefinition, value);
        }


    }


    /**
     * 将合作方请求的值，根据字段定义做转换后设置到上下文
     *
     * @param fieldDefinition
     * @param requestValue
     * @return
     */
    private void putValueByFieldDefinition(Map<String, Object> fields, IFieldDefinition fieldDefinition, Object requestValue) {

        String dataType = fieldDefinition.getDataType();
        String fieldCode = fieldDefinition.getFieldCode();
        try {
            Assign assign = dataTypeAssignMap.get(dataType);
            if (null == assign) {
                return;
            }
            assign.execute(fields, fieldDefinition, requestValue);
        } catch (Exception e) {
            logger.warn(TraceUtils.getFormatTrace() + "决策引擎入参 Failed to set system field, fieldName:{},fieldValue:{},error:{}",
                    fieldCode, requestValue, e.getMessage());
        }
    }

}
