package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldType;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.api.engine.model.policy.PolicyCache;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.util.DateUtil;
import cn.tongdun.kunpeng.common.util.JsonUtil;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            add("sequenceId");
            add("appName");
            add("appType");
            add("eventType");
            add("firstIndustryType");
            add("secondIndustryType");
            add("policy_uuid");
        }
    };

    private static final Set<String> normalFields = Sets.newHashSet("int", "double", "boolean", "string", "float", "integer", "long");

    @Autowired
    private PartnerCache partnerCache;

    @Autowired
    private PolicyCache policyCache;

    @Autowired
    private FieldDefinitionCache fieldDefinitionCache;

    @Override
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {

        Partner partner = partnerCache.get(context.getPartnerCode());

        /*************根据字段的定义，将请求参数设置到上下文中 start**********************/
        //系统字段
        List<FieldDefinition> systemFields = fieldDefinitionCache.getSystemField(context.getEventType(),context.getAppType());
        //扩展字段
        List<FieldDefinition> extendFields = fieldDefinitionCache.getExtendField(context.getPartnerCode(),
                context.getAppName(),context.getEventType());
        setFraudContext(context, request, systemFields, extendFields);
        /*************根据字段的定义，将请求参数设置到上下文中 end**********************/


        /*************决策引擎除字段以外其他参数的获取**********************/
        // 设置合作方的行业信息
        if(partner != null) {
            context.set("firstIndustryType", partner.getIndustryType());
            context.set("secondIndustryType", partner.getSecondIndustryType());
        }
        //调用async,如果async=true，则没有800ms规则引擎超时
        writeSyncInfoToContext(context, request);
        //仿真专用
        writeSimulationArgs(context, request);

        return true;
    }



    /**
     * 使用反射获取bean对象里的所有属性字段
     *
     * @param ctx
     * @param request
     * @param systemFields
     * @param extendFields
     */
    public void setFraudContext(AbstractFraudContext ctx, Map<String, String> request, List<FieldDefinition> systemFields,
                                       List<FieldDefinition> extendFields) {
        if (ctx != null && request != null && systemFields != null) {
            // set system fields
            for (FieldDefinition field : systemFields) {
                String fieldName = field.getFieldName();
                if (excludeFieldName.contains(fieldName)) {
                    continue;
                }
                String fieldType = field.getFieldType();

                String underlineStr = KunpengStringUtils.camel2underline(fieldName);
                String fieldValue = request.get(underlineStr);
                if (StringUtils.isNotBlank(fieldValue)) {
                    try {
                        if (FieldType.STRING.name().equals(fieldType)) {
                            ctx.set(fieldName, fieldValue);
                        } else if (FieldType.INT.name().equals(fieldType)) {
                            ctx.set(fieldName, new Integer(fieldValue));
                        } else if (FieldType.DOUBLE.name().equals(fieldType)) {
                            ctx.set(fieldName, new Double(fieldValue));
                        } else if (FieldType.DATETIME.name().equals(fieldType)) {
                            ctx.set(fieldName, DateUtil.parseDateTime(fieldValue));
                        } else if (FieldType.BOOLEAN.name().equals(fieldType)) {
                            ctx.getSystemFiels().put(fieldName, "true".equalsIgnoreCase(fieldValue) ? true : false);
                        } else if (FieldType.ARRAY.name().equals(fieldType)) {
                            fieldValue = fieldValue.replaceAll("，", ",");
                            ctx.set(fieldName, Arrays.asList(fieldValue.split(",")));
                        } else if (FieldType.OBJECT.name().equals(fieldType)) {
                            try {
                                // 把系统对象的名称加进来，整体组装成大JSON
                                JSONObject fieldInfo = new JSONObject();
                                JSONObject fieldValueJson = JSONObject.parseObject(fieldValue);
                                fieldInfo.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName), fieldValueJson);
                                // 拍平入参JSON
                                Map<String, Object> flattenedJsonInfo = JsonUtil.getFlattenedInfo(fieldInfo.toJSONString());
                                ctx.getSystemFiels().putAll(flattenedJsonInfo);
                                ctx.setObject(true);
                                // 原始JSON也保存一份
                                ctx.getSystemFiels().put(fieldName, camelJson(fieldValueJson));
                            } catch (Exception e) {
                                logger.warn("复杂入参 Invalid JSON error:"+e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("决策引擎入参 Failed to set system field, fieldName:{},fieldValue:{},error:{}",
                                fieldName,fieldValue,e.getMessage());
                    }
                }
            }

            // set extend fields
            if (extendFields != null) {
                for (FieldDefinition field : extendFields) {
                    String name = field.getFieldName();
                    String type = field.getFieldType();
                    String value = request.get(name);
                    if (StringUtils.isBlank(value)) {
                        continue;
                    }

                    try {
                        if (FieldType.STRING.name().equals(type)) {
                            ctx.getCustomFields().put(name, value);
                        } else if (FieldType.INT.name().equals(type)) {
                            ctx.getCustomFields().put(name, (int) Double.parseDouble(value));
                        } else if (FieldType.DOUBLE.name().equals(type)) {
                            ctx.getCustomFields().put(name, Double.parseDouble(value));
                        } else if (FieldType.DATETIME.name().equals(type)) {
                            ctx.getCustomFields().put(name, DateUtil.parseDateTime(value));
                        } else if (FieldType.BOOLEAN.name().equals(type)) {
                            ctx.getCustomFields().put(name, "true".equalsIgnoreCase(value) ? true : false);
                        } else if (FieldType.ARRAY.name().equals(type)) {
                            ctx.getCustomFields().put(name, Arrays.asList(value.replaceAll("，", ",").split(",")));
                        }
                    } catch (Exception e) {
                        logger.warn("决策引擎入参 Failed to set system field, fieldName:{},fieldValue:{},error:{}",
                                name,value,e.getMessage());
                    }
                }
            }
        }
    }


    public static JSONObject camelJson(JSONObject o) {
        JSONObject rst = new JSONObject();
        for (String key : o.keySet()) {
            Object r = o.get(key);
            Object mr = getObject(r);
            rst.put(KunpengStringUtils.underline2camel(key), mr);
        }
        return rst;
    }

    private static Object getObject(Object r) {
        if (isNormalField(r)) {
            return r;
        }
        if (r instanceof JSONObject) {
            JSONObject n = camelJson((JSONObject) r);
            return n;
        }
        if (r instanceof JSONArray) {
            JSONArray aa = (JSONArray) r;
            for (int i = 0; i < aa.size(); i++) {
                aa.set(i, getObject(aa.get(i)));
            }
            return aa;
        }
        return r;
    }

    private static boolean isNormalField(Object o) {
        if (o == null) {
            return false;
        }
        String a = o.getClass().getSimpleName();
        if (o.getClass().isArray()) {
            a = a.substring(0, a.length() - 2);
        }
        return normalFields.contains(a.toLowerCase());
    }


    /**
     * 异步调用信息写入到FraudContext,规则引擎使用
     * @param context
     * @param request
     */
    private void writeSyncInfoToContext(AbstractFraudContext context, Map<String, String> request){
        if(request.containsKey("async")){
            context.setAsync(Boolean.parseBoolean(request.get("async")));
        }
    }

    /**
     * 将仿真调用的参数写入
     */
    private void writeSimulationArgs(AbstractFraudContext context, Map<String, String> request) {
        if(StringUtils.isNotBlank(request.get("simulation_uuid")) && StringUtils.equalsIgnoreCase(request.get("td_td_simulation"), "1")) {
            context.set("simulationUuid", request.get("simulation_uuid"));
            if(!StringUtils.equalsIgnoreCase("all", request.get("simulation_partner"))) {
                context.setSimulatePartnerCode(request.get("simulation_partner"));
                context.setSimulateAppName(request.get("simulation_app"));
            }

            if(StringUtils.isNotBlank(request.get("td_sample_data_id"))) {
                context.set("td_sample_data_id", request.get("td_sample_data_id"));
            }
            context.setSimulateSequenceId(request.get("simulation_seqid"));
            context.setSimulation(true);
        }
    }
}
