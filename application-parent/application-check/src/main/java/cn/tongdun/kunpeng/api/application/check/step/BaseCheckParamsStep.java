package cn.tongdun.kunpeng.api.application.check.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinition;
import cn.tongdun.kunpeng.api.engine.model.field.FieldDefinitionCache;
import cn.tongdun.kunpeng.api.engine.model.field.FieldType;
import cn.tongdun.kunpeng.client.data.RiskResponse;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.ReasonCode;
import cn.tongdun.kunpeng.common.util.KunpengStringUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public boolean invoke(AbstractFraudContext context, RiskResponse response, Map<String, String> request) {



        // 检查必填字段，字段类型
        StringBuilder sbType = new StringBuilder();
        StringBuilder sbFormat = new StringBuilder();
        StringBuilder sbOvermax = new StringBuilder();
        List<FieldDefinition> sysFields = fieldDefinitionCache.getSystemField(context.getEventType(),context.getAppType());
        List<FieldDefinition> extFields = fieldDefinitionCache.getExtendField(context.getPartnerCode(),context.getAppName(),context.getEventType());
        if (sysFields != null) {
            checkParams(request,sysFields,sbType,sbFormat,sbOvermax);
        }
        if (extFields != null) {
            checkParams(request,sysFields,sbType,sbFormat,sbOvermax);
        }


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
            response.setReason_code(sb.toString());
        } else {
            return true;
        }

        return false;
    }

    /**
     * 检查参数格式是否正确
     * @param request
     * @param fields
     * @param sbType
     * @param sbFormat
     * @param sbOvermax
     */
    private void checkParams(Map<String, String> request,List<FieldDefinition> fields,
                             StringBuilder sbType, StringBuilder sbFormat, StringBuilder sbOvermax){
        for (FieldDefinition fieldDefinition : fields) {
            String name = fieldDefinition.getFieldName();
            String type = fieldDefinition.getFieldType();

            String val = request.get(KunpengStringUtils.camel2underline(name));

            if (StringUtils.isNotBlank(val)) {
                if (FieldType.INT.name().equals(type) || FieldType.DOUBLE.name().equals(type)) {
                    if (!KunpengStringUtils.isNumeric(val)) {
                        if (sbType.length() > 0) {
                            sbType.append(",");
                        }
                        sbType.append(name);
                    }
                } else if (FieldType.DATETIME.name().equals(type)) {
                    if (!KunpengStringUtils.isDate(val)) {
                        if (sbType.length() > 0){
                            sbType.append(",");
                        }
                        sbType.append(name);
                    }
                } else if (FieldType.BOOLEAN.name().equals(type)) {
                    if (!"true".equalsIgnoreCase(val) && !"false".equalsIgnoreCase(val)) {
                        if (sbType.length() > 0) {
                            sbType.append(",");
                        }
                        sbType.append(name);
                    }
                } else if (FieldType.ARRAY.name().equals(type)) {
                    if (val.replaceAll("，", ",").split(",").length > 20) {
                        if (sbOvermax.length() > 0) {
                            sbOvermax.append(",");
                        }
                        sbOvermax.append(name);
                    }
                }
            }
        }
    }
}
