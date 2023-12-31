package cn.tongdun.kunpeng.api.application.ext;

import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 下午5:16
 */
@Extension(business = BizScenario.DEFAULT,tenant = "haiwai",partner = BizScenario.DEFAULT)
public class HaiwaiCreateRiskRequestExtPt implements ICreateRiskRequestExtPt {

    private static final Field[] fields = HaiwaiCreateRiskRequestExtPt.class.getDeclaredFields();
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

    public static final String        SERVICE_TYPE           = "service_type";
    public static final String        TEST_FLAG              = "test_flag";
    public static final String        PARTNER_CODE           = "partner_code";
    public static final String        SECRET_KEY             = "secret_key";
    public static final String        APP_NAME               = "app_name";
    public static final String        EVENT_ID               = "event_id";
    public static final String        POLICY_VERSION         = "policy_version";
    public static final String        SEQ_ID                 = "seq_id";
    public static final String        X_SEQUENCE_ID          = "x-sequence-id";
    public static final String        RESP_DETAIL_TYPE       = "resp_detail_type";
    public static final String        RECALL                 = "recall";
    public static final String        RECALL_SEQ_ID          = "recall_sequence_id";
    public static final String        TOKEN_ID               = "token_id";
    public static final String        BLACK_BOX              = "black_box";
    public static final String        REQUEST_ID             = "request_id";
    public static final String        X_REQUEST_ID           = "x-request-id";
    public static final String        ASYNC                  = "async";

    public static final String        SIMULATION_PARTNER     = "simulation_partner";
    public static final String        SIMULATION_APP         = "simulation_app";
    public static final String        SIMULATION_UUID        = "simulation_uuid";
    public static final String        SIMULATION_SEQ_ID      = "simulationSeqId";
    public static final String        TD_SAMPLE_DATA_ID      = "td_sample_data_id";



    @Override
    public RiskRequest createRiskRequest(Map<String, Object> request){
        RiskRequest riskRequest = new RiskRequest();
        riskRequest.setPartnerCode((String)request.get(PARTNER_CODE));
        riskRequest.setSecretKey((String)request.get(SECRET_KEY));
        riskRequest.setAppName((String)request.get(APP_NAME));
        riskRequest.setBlackBox((String)request.get(BLACK_BOX));
        riskRequest.setEventId((String)request.get(EVENT_ID));
        riskRequest.setPolicyVersion((String)request.get(POLICY_VERSION));
        riskRequest.setRecall(StringUtils.equalsIgnoreCase((String)request.get(RECALL), "true"));
        riskRequest.setRecallSeqId((String)request.get(RECALL_SEQ_ID));
        riskRequest.setRespDetailType((String)request.get(RESP_DETAIL_TYPE));
        riskRequest.setServiceType((String)request.get(SERVICE_TYPE));
        riskRequest.setSeqId((String)request.get(SEQ_ID));
        if(StringUtils.isBlank(riskRequest.getSeqId())){
            riskRequest.setSeqId((String)request.get(X_SEQUENCE_ID));
        }
        riskRequest.setTokenId((String)request.get(TOKEN_ID));
        riskRequest.setRequestId((String)request.get(REQUEST_ID));
        if(StringUtils.isBlank(riskRequest.getRequestId())){
            riskRequest.setRequestId((String)request.get(X_REQUEST_ID));
        }
        riskRequest.setTestFlag(StringUtils.equalsIgnoreCase((String)request.get(TEST_FLAG), "true"));
        riskRequest.setAsync(StringUtils.equalsIgnoreCase((String)request.get(ASYNC), "true"));
        riskRequest.setSimulationApp((String)request.get(SIMULATION_APP));
        riskRequest.setSimulationPartner((String)request.get(SIMULATION_PARTNER));
        riskRequest.setSimulationUuid((String)request.get(SIMULATION_UUID));
        riskRequest.setSimulationSeqId((String)request.get(SIMULATION_SEQ_ID));
        riskRequest.setTdSampleDataId((String)request.get(TD_SAMPLE_DATA_ID));

        riskRequest.setFieldValues(createFieldValues(request));

        return riskRequest;
    }


    private Map<String,Object> createFieldValues(Map<String, Object> request){
        Map<String,Object> fieldValues =  new HashMap<>();
        request.forEach((key,value)->{
            //blackBox特殊处理下，不然注入不到context
            if(!fieldNames.contains(key) || StringUtils.equals(key, BLACK_BOX)){
                fieldValues.put(key,value);
            }
        });
        return fieldValues;
    }


}
