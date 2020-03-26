package cn.tongdun.kunpeng.client.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/26 下午5:17
 */
public class CustomSerializer extends StdSerializer<RiskRequest> {

    public CustomSerializer(){
        super(RiskRequest.class);
    }
    @Override
    public void serialize(RiskRequest riskRequest, JsonGenerator jgen,
                          SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("partnerCode", riskRequest.getPartnerCode());
        jgen.writeStringField("secretKey", desensitization(riskRequest.getSecretKey()));

        jgen.writeStringField("eventId", riskRequest.getEventId());
        jgen.writeStringField("policyVersion", riskRequest.getPolicyVersion());

        jgen.writeStringField("serviceType", riskRequest.getServiceType());
        jgen.writeStringField("seqId", riskRequest.getSeqId());

        jgen.writeStringField("requestId", riskRequest.getRequestId());
        if(riskRequest.getEventOccurTime() != null) {
            jgen.writeNumberField("eventOccurTime", riskRequest.getEventOccurTime().getTime());
        }

        jgen.writeBooleanField("testFlag", riskRequest.isTestFlag());
        jgen.writeBooleanField("async", riskRequest.isAsync());

        jgen.writeStringField("tokenId", riskRequest.getTokenId());
        jgen.writeStringField("blackBox", riskRequest.getBlackBox());

        jgen.writeStringField("respDetailType", riskRequest.getRespDetailType());
        jgen.writeBooleanField("recall", riskRequest.isRecall());

        jgen.writeStringField("recallSeqId", riskRequest.getRecallSeqId());
        jgen.writeStringField("simulationPartner", riskRequest.getSimulationPartner());


        jgen.writeStringField("simulationSeqId", riskRequest.getSimulationSeqId());
        jgen.writeStringField("simulationUuid", riskRequest.getSimulationUuid());

        jgen.writeStringField("tdSampleDataId", riskRequest.getTdSampleDataId());
        jgen.writeObjectField("fieldValues", riskRequest.getFieldValues());

        jgen.writeObjectField("extAttrs", riskRequest.getExtAttrs());

        jgen.writeEndObject();
    }


    /**
     * secretKey脱敏
     * @param value
     * @return
     */
    private String desensitization(String value){
        if(value == null){
            return null;
        }
        if (StringUtils.isBlank(value.toString()) || StringUtils.length(value.toString()) != 32) {
            return value;
        }
        return StringUtils.left(value, 6).concat(
                StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(value, 6),
                        StringUtils.length(value), "*"), "******"));
    }
}