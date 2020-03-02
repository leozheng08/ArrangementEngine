package cn.tongdun.kunpeng.api.application.ext;

import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import cn.tongdun.tdframework.core.extension.IExtensionPoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 下午5:16
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class DefaultCreateRiskRequestExtPt implements ICreateRiskRequestExtPt {

    private static final Field[] fields = DefaultCreateRiskRequestExtPt.class.getDeclaredFields();
    private static final Set<String> fieldNames = new HashSet<>(fields.length);
    static {
        try {
            Set<String> includeTypes = new HashSet<String>() {{ add("string"); }};
            for (Field field : fields) {
                String simTypeName = field.getType().getSimpleName();
                if (includeTypes.contains(simTypeName.toLowerCase())) {
                    fieldNames.add((String)field.get(null));
                }
            }
        } catch (Exception e){}
    }
    public static final String        SERVICE_TYPE           = "serviceType";
    public static final String        TEST_FLAG              = "testFlag";
    public static final String        PARTNER_CODE           = "partnerCode";
    public static final String        SECRET_KEY             = "secretKey";
    public static final String        EVENT_ID               = "eventId";
    public static final String        POLICY_VERSION         = "policyVersion";
    public static final String        SEQ_ID                 = "seqId";
    public static final String        X_SEQUENCE_ID          = "x-sequence-id";
    public static final String        RESP_DETAIL_TYPE       = "respDetailType";
    public static final String        RECALL                 = "recall";
    public static final String        RECALL_SEQUENCE_ID     = "recallSequenceId";
    public static final String        TOKEN_ID               = "tokenId";
    public static final String        BLACK_BOX              = "blackBox";
    public static final String        REQUEST_ID             = "requestId";
    public static final String        X_REQUEST_ID           = "x-request-id";
    public static final String        ASYNC                  = "async";

    public static final String        SIMULATION_PARTNER     = "simulationPartner";
    public static final String        SIMULATION_APP         = "simulationApp";
    public static final String        SIMULATION_UUID        = "simulationUuid";
    public static final String        SIMULATION_SEQ_ID      = "simulationSeqId";
    public static final String        TD_SAMPLE_DATA_ID      = "tdSampleDataId";




    @Override
    public RiskRequest createRiskRequest(Map<String, String> request){
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.setPartnerCode(request.get(PARTNER_CODE));
        riskRequest.setSecretKey(request.get(SECRET_KEY));
        riskRequest.setBlackBox(request.get(BLACK_BOX));
        riskRequest.setEventId(request.get(EVENT_ID));
        riskRequest.setPolicyVersion(request.get(POLICY_VERSION));
        riskRequest.setRecall(StringUtils.equalsIgnoreCase(request.get(RECALL), "true"));
        riskRequest.setRecallSequenceId(request.get(RECALL_SEQUENCE_ID));
        riskRequest.setRespDetailType(request.get(RESP_DETAIL_TYPE));
        riskRequest.setServiceType(request.get(SERVICE_TYPE));
        riskRequest.setSequenceId(request.get(SEQ_ID));
        if(StringUtils.isBlank(riskRequest.getSequenceId())){
            riskRequest.setSequenceId(request.get(X_SEQUENCE_ID));
        }
        riskRequest.setTokenId(request.get(TOKEN_ID));
        riskRequest.setRequestId(request.get(REQUEST_ID));
        riskRequest.setTestFlag(StringUtils.equalsIgnoreCase(request.get(TEST_FLAG), "true"));
        riskRequest.setAsync(StringUtils.equalsIgnoreCase(request.get(ASYNC), "true"));
        riskRequest.setSimulationApp(request.get(SIMULATION_APP));
        riskRequest.setSimulationPartner(request.get(SIMULATION_PARTNER));
        riskRequest.setSimulationUuid(request.get(SIMULATION_UUID));
        riskRequest.setSimulationSeqId(request.get(SIMULATION_SEQ_ID));
        riskRequest.setTdSampleDataId(request.get(TD_SAMPLE_DATA_ID));

        riskRequest.setFieldValues(createFieldValues(request));

        return riskRequest;
    }


    private Map<String,Object> createFieldValues(Map<String, String> request){
        Map<String,Object> fieldValues =  new HashMap<>();
        request.forEach((key,value)->{
            if(!fieldNames.contains(key)){
                fieldValues.put(key,value);
            }
        });
        return fieldValues;
    }
}
