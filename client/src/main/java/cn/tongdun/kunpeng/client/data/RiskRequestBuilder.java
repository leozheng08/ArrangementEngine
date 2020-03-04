package cn.tongdun.kunpeng.client.data;

import java.util.Date;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 下午1:11
 */
public class RiskRequestBuilder {
    private RiskRequest riskRequest = new RiskRequest();
    

    public RiskRequestBuilder setPartnerCode(String partnerCode) {
        riskRequest.setPartnerCode(partnerCode);
        return this;
    }
    

    public RiskRequestBuilder setSecretKey(String secretKey) {
        riskRequest.setSecretKey(secretKey);
        return this;
    }
    

    public RiskRequestBuilder setEventId(String eventId) {
        riskRequest.setEventId(eventId);
        return this;
    }
    
    public RiskRequestBuilder setPolicyVersion(String policyVersion) {
        riskRequest.setPolicyVersion(policyVersion);
        return this;
    }
    

    public RiskRequestBuilder setServiceType(String serviceType) {
        riskRequest.setServiceType(serviceType);
        return this;
    }

    public RiskRequestBuilder setSeqId(String seqId) {
        riskRequest.setSeqId(seqId);
        return this;
    }


    public RiskRequestBuilder setRequestId(String requestId) {
        riskRequest.setRequestId(requestId);
        return this;
    }
    

    public RiskRequestBuilder setEventOccurTime(Date eventOccurTime) {
        riskRequest.setEventOccurTime(eventOccurTime);
        return this;
    }
    

    public RiskRequestBuilder setTestFlag(boolean testFlag) {
        riskRequest.setTestFlag(testFlag);
        return this;
    }

    public RiskRequestBuilder setAsync(boolean async) {
        riskRequest.setAsync(async);
        return this;
    }
    
    public RiskRequestBuilder setTokenId(String tokenId) {
        riskRequest.setTokenId(tokenId);
        return this;
    }


    public RiskRequestBuilder setBlackBox(String blackBox) {
        riskRequest.setBlackBox(blackBox);
        return this;
    }


    public RiskRequestBuilder setRespDetailType(String respDetailType) {
        riskRequest.setRespDetailType(respDetailType);
        return this;
    }


    public RiskRequestBuilder setRecall(boolean recall) {
        riskRequest.setRecall(recall);
        return this;
    }


    public RiskRequestBuilder setRecallSeqId(String recallSeqId) {
        riskRequest.setRecallSeqId(recallSeqId);
        return this;
    }

    public RiskRequestBuilder setSimulationPartner(String simulationPartner) {
        riskRequest.setSimulationPartner(simulationPartner);
        return this;
    }

    public RiskRequestBuilder setSimulationApp(String simulationApp) {
        riskRequest.setSimulationApp(simulationApp);
        return this;
    }


    public RiskRequestBuilder setSimulationSeqId(String simulationSeqId) {
        riskRequest.setSimulationSeqId(simulationSeqId);
        return this;
    }

    public RiskRequestBuilder setTdSampleDataId(String tdSampleDataId) {
        riskRequest.setTdSampleDataId(tdSampleDataId);
        return this;
    }

    public RiskRequestBuilder setFieldValue(String fieldCode, Object value){
        riskRequest.setFieldValue(fieldCode,value);
        return this;
    }

    public RiskRequest getRiskRequest() {
        return riskRequest;
    }
}
