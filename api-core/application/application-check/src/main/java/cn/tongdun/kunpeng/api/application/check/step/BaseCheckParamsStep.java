package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.IFieldDefinition;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDataType;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/2/20 下午3:22
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 600)
public class BaseCheckParamsStep implements IRiskStep {

    @Autowired
    FieldDefinitionCache fieldDefinitionCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {



        // 检查必填字段，字段类型
        StringBuilder sbType = new StringBuilder();
        StringBuilder sbFormat = new StringBuilder();
        StringBuilder sbOvermax = new StringBuilder();

        context.setSystemFieldMap(fieldDefinitionCache.getSystemField(context.getEventType()));

        Map<String, IFieldDefinition> extendFieldMap = fieldDefinitionCache.getExtendField(context.getPartnerCode(),context.getEventType());
        if (extendFieldMap != null && extendFieldMap.size() > 0) {
            context.setExtendFieldMap(extendFieldMap);
        }

        checkParamFromRequest(request,context,sbType,sbFormat,sbOvermax);


        StringBuilder sb = new StringBuilder();
        if (sbType.length() > 0) {
            sb.append(ReasonCode.PARAM_DATA_TYPE_ERROR.toString());
            sb.append(",").append(sbType.toString());
        }
        else if (sbFormat.length() > 0) {
            sb.append(ReasonCode.PARAM_FORMAT_ERROR.toString());
            sb.append(",").append(sbFormat.toString());
        }
        else if (sbOvermax.length() > 0) {
            sb.append(ReasonCode.PARAM_OVER_MAX_LEN.toString());
            sb.append(",").append(sbOvermax.toString());
        }
        if (sb.length() > 0) {
            response.setReasonCode(sb.toString());
        } else {
            return true;
        }

        return false;
    }

    private void checkParamFromRequest(RiskRequest request, AbstractFraudContext context,
                                       StringBuilder sbType, StringBuilder sbFormat, StringBuilder sbOvermax) {
        if (request.getFieldValues() == null || request.getFieldValues().isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : request.getFieldValues().entrySet()) {
            //值检查
            Object val=entry.getValue();
            if(val == null){
                continue;
            }
            if(val instanceof String && StringUtils.isBlank((String)val)){
                continue;
            }

            IFieldDefinition fieldDefinition=context.getFieldDefinition(entry.getKey());
            if (null==fieldDefinition){
                String stardandCode=CamelAndUnderlineConvertUtil.underline2camel(entry.getKey());
                if (null==stardandCode){
                    continue;
                }
                fieldDefinition=context.getFieldDefinition(stardandCode);
            }

            if (null==fieldDefinition){
                continue;
            }

            if (FieldDataType.INT.name().equals(fieldDefinition.getDataType()) || FieldDataType.DOUBLE.name().equals(fieldDefinition.getDataType())) {
                if(val instanceof Number ){
                    continue;
                }
                if (!KunpengStringUtils.isNumeric(val.toString())) {
                    if (sbType.length() > 0) {
                        sbType.append(",");
                    }
                    sbType.append(fieldDefinition.getFieldCode());
                }
            } else if (FieldDataType.DATETIME.name().equals(fieldDefinition.getDataType())) {
                if(val instanceof Date){
                    continue;
                }
                if (!KunpengStringUtils.isDate(val.toString())) {
                    if (sbType.length() > 0){
                        sbType.append(",");
                    }
                    sbType.append(fieldDefinition.getFieldCode());
                }
            } else if (FieldDataType.BOOLEAN.name().equals(fieldDefinition.getDataType())) {
                if(val instanceof Boolean){
                    continue;
                }
                if (!"true".equalsIgnoreCase(val.toString()) && !"false".equalsIgnoreCase(val.toString())) {
                    if (sbType.length() > 0) {
                        sbType.append(",");
                    }
                    sbType.append(fieldDefinition.getFieldCode());
                }
            } else if (FieldDataType.ARRAY.name().equals(fieldDefinition.getDataType())) {
                if(val instanceof List || val instanceof Object[]){
                    continue;
                }
                if (val.toString().replaceAll("，", ",").split(",").length > 20) {
                    if (sbOvermax.length() > 0) {
                        sbOvermax.append(",");
                    }
                    sbOvermax.append(fieldDefinition.getFieldCode());
                }
            }

        }
    }
    /**
     * 检查参数格式是否正确
     * @param request
     * @param fields
     * @param sbType
     * @param sbFormat
     * @param sbOvermax
     */
    private void checkParams(RiskRequest request,Collection<FieldDefinition> fields,
                             StringBuilder sbType, StringBuilder sbFormat, StringBuilder sbOvermax){
        for (FieldDefinition fieldDefinition : fields) {
            String fieldCode = fieldDefinition.getFieldCode();
            String dataType = fieldDefinition.getDataType();

            Object val = request.getFieldValues().get(fieldCode);
            if(val == null){
                continue;
            }
            if(val instanceof String && StringUtils.isBlank((String)val)){
                continue;
            }
            if (FieldDataType.INT.name().equals(dataType) || FieldDataType.DOUBLE.name().equals(dataType)) {
                if(val instanceof Number ){
                    continue;
                }
                if (!KunpengStringUtils.isNumeric(val.toString())) {
                    if (sbType.length() > 0) {
                        sbType.append(",");
                    }
                    sbType.append(fieldCode);
                }
            } else if (FieldDataType.DATETIME.name().equals(dataType)) {
                if(val instanceof Date){
                    continue;
                }
                if (!KunpengStringUtils.isDate(val.toString())) {
                    if (sbType.length() > 0){
                        sbType.append(",");
                    }
                    sbType.append(fieldCode);
                }
            } else if (FieldDataType.BOOLEAN.name().equals(dataType)) {
                if(val instanceof Boolean){
                    continue;
                }
                if (!"true".equalsIgnoreCase(val.toString()) && !"false".equalsIgnoreCase(val.toString())) {
                    if (sbType.length() > 0) {
                        sbType.append(",");
                    }
                    sbType.append(fieldCode);
                }
            } else if (FieldDataType.ARRAY.name().equals(dataType)) {
                if(val instanceof List || val instanceof Object[]){
                    continue;
                }
                if (val.toString().replaceAll("，", ",").split(",").length > 20) {
                    if (sbOvermax.length() > 0) {
                        sbOvermax.append(",");
                    }
                    sbOvermax.append(fieldCode);
                }
            }
        }
    }
}
