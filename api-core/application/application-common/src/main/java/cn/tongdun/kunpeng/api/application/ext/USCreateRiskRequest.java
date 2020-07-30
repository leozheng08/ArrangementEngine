package cn.tongdun.kunpeng.api.application.ext;

import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: yuanhang
 * @date: 2020-07-27 19:11
 **/
@Extension(business = BizScenario.DEFAULT,tenant = "us",partner = "globalegrow")
public class USCreateRiskRequest implements ICreateRiskRequestExtPt {
    private Logger logger = LoggerFactory.getLogger(USCreateRiskRequest.class);
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
    public static final String        SERVICE_TYPE           = "SERVICETYPE";
    public static final String        TEST_FLAG              = "TESTFLAG";
    public static final String        PARTNER_CODE           = "PARTNERCODE";
    public static final String        SECRET_KEY             = "SECREKEY";
    public static final String        APP_NAME               = "APPNAME";
    public static final String        EVENT_ID               = "EVENTID";
    public static final String        POLICY_VERSION         = "POLICY_VERSION";
    public static final String        SEQ_ID                 = "seqId";
    public static final String        X_SEQUENCE_ID          = "x-sequence-id";
    public static final String        RESP_DETAIL_TYPE       = "RESPDETAIL_TYPE";
    public static final String        RECALL                 = "RECALL";
    public static final String        RECALL_SEQ_ID          = "RECALLSEQ_ID";
    public static final String        TOKEN_ID               = "TOKEN_ID";
    public static final String        BLACK_BOX              = "BLACKBOX";
    public static final String        REQUEST_ID             = "REQUEST_ID";
    public static final String        X_REQUEST_ID           = "X_REQUEST_ID";
    public static final String        ASYNC                  = "ASYNC";

    public static final String        SIMULATION_PARTNER     = "SIMULATION_PARTNER";
    public static final String        SIMULATION_APP         = "SIMULATION_APP";
    public static final String        SIMULATION_UUID        = "SIMULATION_UUID";
    public static final String        SIMULATION_SEQ_ID      = "SIMULATION_SEQ_ID";
    public static final String        TD_SAMPLE_DATA_ID      = "TD_SAMPLE_DATA_ID";




    @Override
    public RiskRequest createRiskRequest(Map<String, String> request){
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.setPartnerCode(request.get(PARTNER_CODE));
        riskRequest.setSecretKey(request.get(SECRET_KEY));
        riskRequest.setAppName(request.get(APP_NAME));
        riskRequest.setBlackBox(request.get(BLACK_BOX));
        riskRequest.setEventId(request.get(EVENT_ID));
        riskRequest.setPolicyVersion(request.get(POLICY_VERSION));
        riskRequest.setRecall(StringUtils.equalsIgnoreCase(request.get(RECALL), "true"));
        riskRequest.setRecallSeqId(request.get(RECALL_SEQ_ID));
        riskRequest.setRespDetailType(request.get(RESP_DETAIL_TYPE));
        riskRequest.setServiceType(request.get(SERVICE_TYPE));
        riskRequest.setSeqId(request.get(SEQ_ID));
        if(StringUtils.isBlank(riskRequest.getSeqId())){
            riskRequest.setSeqId(request.get(X_SEQUENCE_ID));
            if(StringUtils.isBlank(riskRequest.getSeqId())) {
                logger.error("get blank seqId from uig:{}", request);
            }
        }
        riskRequest.setTokenId(request.get(TOKEN_ID));
        riskRequest.setRequestId(request.get(REQUEST_ID));
        if(StringUtils.isBlank(riskRequest.getRequestId())){
            riskRequest.setRequestId(request.get(X_REQUEST_ID));
        }
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
