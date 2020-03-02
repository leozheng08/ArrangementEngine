package cn.tongdun.kunpeng.api.application.ext;

import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 下午5:16
 */
@Extension(business = BizScenario.DEFAULT,tenant = "haiwai",partner = BizScenario.DEFAULT)
public class HaiwaiCreateRiskRequestExtPt implements ICreateRiskRequestExtPt {

    public static final String        SERVICE_TYPE           = "service_type";
    public static final String        TEST_FLAG              = "test_flag";
    public static final String        PARTNER_CODE           = "partner_code";
    public static final String        SECRET_KEY             = "secret_key";
    public static final String        EVENT_ID               = "event_id";
    public static final String        POLICY_VERSION         = "policy_version";
    public static final String        SEQ_ID                 = "seq_id";
    public static final String        X_SEQUENCE_ID          = "x-sequence-id";
    public static final String        RESP_DETAIL_TYPE       = "resp_detail_type";
    public static final String        RECALL                 = "recall";
    public static final String        RECALL_SEQUENCE_ID     = "recall_sequence_id";
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
        return riskRequest;
    }


}
