package cn.tongdun.kunpeng.api.common.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Rosy on 16/6/16.
 */
public class PartnerIndustryDO implements Serializable {

    private static final long serialVersionUID = 7402428066032308418L;
    private String       partnerCode;
    private String       firstIndustryType;
    private String       secondIndustryType;
    private List<String> labels;

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getFirstIndustryType() {
        return firstIndustryType;
    }

    public void setFirstIndustryType(String firstIndustryType) {
        this.firstIndustryType = firstIndustryType;
    }

    public String getSecondIndustryType() {
        return secondIndustryType;
    }

    public void setSecondIndustryType(String secondIndustryType) {
        this.secondIndustryType = secondIndustryType;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
