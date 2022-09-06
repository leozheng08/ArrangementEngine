package cn.tongdun.kunpeng.api.basedata.service.cardbin;

import java.io.Serializable;

public class CardBinTO implements Serializable {
    private static final long serialVersionUID = -3154659039726598474L;

    /**
     * 品牌
     */
    private String cardBrand;

    /**
     * 卡分类
     */
    private String cardCategory;

    /**
     * BIN码
     */
    private Long bin;

    /**
     * 卡类型，如借记卡，信用卡等
     */
    private String cardType;

    /**
     * 国家名称
     */
    private String countryName;

    /**
     * ISO A2
     */
    private String isoA2;

    /**
     * ISO A3
     */
    private String isoA3;

    /**
     * 国家ISO名称
     */
    private String isoName;

    /**
     * ISO代码
     */
    private Integer isoNumber;

    /**
     * 颁发机构
     */
    private String issuingOrg;

    /**
     * 颁发机构联系方式
     */
    private String issuingOrgPhone;

    /**
     * 颁发机构网
     */
    private String issuingOrgWeb;

    /**
     * PAN长度
     */
    private Integer panLength;

    /**
     * 用途，商用or个人
     */
    private String purposeFlag;

    /**
     * regulated (Y) or unregulated (N) BIN
     */
    private String regulated;

    public Long getBin() {
        return bin;
    }

    public void setBin(Long bin) {
        this.bin = bin;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getIssuingOrg() {
        return issuingOrg;
    }

    public void setIssuingOrg(String issuingOrg) {
        this.issuingOrg = issuingOrg;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardCategory() {
        return cardCategory;
    }

    public void setCardCategory(String cardCategory) {
        this.cardCategory = cardCategory;
    }

    public String getIsoName() {
        return isoName;
    }

    public void setIsoName(String isoName) {
        this.isoName = isoName;
    }

    public String getIsoA2() {
        return isoA2;
    }

    public void setIsoA2(String isoA2) {
        this.isoA2 = isoA2;
    }

    public String getIsoA3() {
        return isoA3;
    }

    public void setIsoA3(String isoA3) {
        this.isoA3 = isoA3;
    }

    public Integer getIsoNumber() {
        return isoNumber;
    }

    public void setIsoNumber(Integer isoNumber) {
        this.isoNumber = isoNumber;
    }

    public String getIssuingOrgWeb() {
        return issuingOrgWeb;
    }

    public void setIssuingOrgWeb(String issuingOrgWeb) {
        this.issuingOrgWeb = issuingOrgWeb;
    }

    public String getIssuingOrgPhone() {
        return issuingOrgPhone;
    }

    public void setIssuingOrgPhone(String issuingOrgPhone) {
        this.issuingOrgPhone = issuingOrgPhone;
    }

    public Integer getPanLength() {
        return panLength;
    }

    public void setPanLength(Integer panLength) {
        this.panLength = panLength;
    }

    public String getPurposeFlag() {
        return purposeFlag;
    }

    public void setPurposeFlag(String purposeFlag) {
        this.purposeFlag = purposeFlag;
    }

    public String getRegulated() {
        return regulated;
    }

    public void setRegulated(String regulated) {
        this.regulated = regulated;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "CardBinTO{" +
                "bin=" + bin +
                ", cardBrand='" + cardBrand + '\'' +
                ", issuingOrg='" + issuingOrg + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardCategory='" + cardCategory + '\'' +
                ", isoName='" + isoName + '\'' +
                ", isoA2='" + isoA2 + '\'' +
                ", isoA3='" + isoA3 + '\'' +
                ", isoNumber=" + isoNumber +
                ", issuingOrgWeb='" + issuingOrgWeb + '\'' +
                ", issuingOrgPhone='" + issuingOrgPhone + '\'' +
                ", panLength=" + panLength +
                ", purposeFlag='" + purposeFlag + '\'' +
                ", regulated='" + regulated + '\'' +
                ", countryName='" + countryName + '\'' +
                '}';
    }
}
