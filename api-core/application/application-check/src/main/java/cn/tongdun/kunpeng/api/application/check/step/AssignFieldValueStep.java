package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.check.step.assign.*;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDataType;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 设置上下文中的字段值
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 1100)
public class AssignFieldValueStep implements IRiskStep {

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

    private static final Map<String,Assign> dataTypeAssignMap=new HashMap<>(7);
    static {
        dataTypeAssignMap.put(FieldDataType.OBJECT.name(),new ObjectAssign());
        dataTypeAssignMap.put(FieldDataType.ARRAY.name(),new ArrayAssign());
        dataTypeAssignMap.put(FieldDataType.BOOLEAN.name(),new BooleanAssign());
        dataTypeAssignMap.put(FieldDataType.DATETIME.name(),new DatetimeAssign());
        dataTypeAssignMap.put(FieldDataType.DOUBLE.name(),new DoubleAssign());
        dataTypeAssignMap.put(FieldDataType.INT.name(),new IntAssign());
        dataTypeAssignMap.put(FieldDataType.STRING.name(),new StringAssign());
    }

    @Autowired
    private PartnerCache partnerCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        Partner partner = partnerCache.get(context.getPartnerCode());

        /*************根据字段的定义，将请求参数设置到上下文中 start**********************/
        setFraudContext(context,request);
        /*************根据字段的定义，将请求参数设置到上下文中 end**********************/


        /*************决策引擎除字段以外其他参数的获取**********************/
        //service-type=creditcloud时决策接口直接返回详情
        String serviceType = request.getServiceType();
        if (StringUtils.isBlank(serviceType)) {
            serviceType = "professional";
            context.setServiceType(serviceType);
        }

        //判断是否测试数据
        context.setTestFlag(request.isTestFlag());

        // 设置合作方的行业信息
        if(partner != null) {
            context.set("firstIndustryType", partner.getIndustryType());
            context.set("secondIndustryType", partner.getSecondIndustryType());
        }

        //调用async,如果async=true，则没有800ms规则引擎超时
        context.setAsync(request.isAsync());

        //如果客户有传事件发生时间，为客户传的为准
        Object eventOccurTime = context.getFieldValues().get("eventOccurTime");
        if(eventOccurTime != null && eventOccurTime instanceof Date){
            context.setEventOccurTime((Date)eventOccurTime);
        }
        return true;
    }


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

            putValueByFieldDefinition(ctx.getFieldValues(),fieldDefinition,entry.getValue());
        }
    }



    /**
     * 将合作方请求的值，根据字段定义做转换后设置到上下文
     * @param fieldDefinition
     * @param requestValue
     * @return
     */
    private void putValueByFieldDefinition(Map<String, Object> fields,IFieldDefinition fieldDefinition,Object requestValue){

        String dataType = fieldDefinition.getDataType();
        String fieldCode = fieldDefinition.getFieldCode();
        try {
            Assign assign=dataTypeAssignMap.get(dataType);
            if (null==assign){
                return;
            }
            assign.execute(fields,fieldDefinition,requestValue);
        } catch (Exception e) {
            logger.warn(TraceUtils.getFormatTrace()+"决策引擎入参 Failed to set system field, fieldName:{},fieldValue:{},error:{}",
                    fieldCode, requestValue, e.getMessage());
        }
    }

}