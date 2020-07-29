package cn.tongdun.kunpeng.api.engine.model.compare;

import cn.tongdun.ddd.common.domain.CommonEntity;
import java.util.Date;

/**
 * @author: yuanhang
 * @date: 2020-07-27 15:53
 **/
public class CompareInfo extends CommonEntity {
    private Long id;

    private String seqId;

    private String partnerCode;

    private Date gmtCreate;

    private Date gmtModify;

    private String tcRequest;

    private String tcReponse;

    private String kpRequest;

    private String kpReponse;

    public String getTcRequest() {
        return tcRequest;
    }

    public void setTcRequest(String tcRequest) {
        this.tcRequest = tcRequest == null ? null : tcRequest.trim();
    }

    public String getTcReponse() {
        return tcReponse;
    }

    public void setTcReponse(String tcReponse) {
        this.tcReponse = tcReponse == null ? null : tcReponse.trim();
    }

    public String getKpRequest() {
        return kpRequest;
    }

    public void setKpRequest(String kpRequest) {
        this.kpRequest = kpRequest == null ? null : kpRequest.trim();
    }

    public String getKpReponse() {
        return kpReponse;
    }

    public void setKpReponse(String kpReponse) {
        this.kpReponse = kpReponse == null ? null : kpReponse.trim();
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId == null ? null : seqId.trim();
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode == null ? null : partnerCode.trim();
    }
}
