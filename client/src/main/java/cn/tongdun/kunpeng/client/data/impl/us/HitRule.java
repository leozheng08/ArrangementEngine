package cn.tongdun.kunpeng.client.data.impl.us;

import java.util.List;

/**
 * @author: yuanhang
 * @date: 2020-06-17 15:52
 **/
public class HitRule {

    private String uuid;

    private String name;

    private String enName;

    private String decision;

    private Integer score;

    private String dealType;

    private List<RuleDetailData> ruleDetailDataList;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public List<RuleDetailData> getRuleDetailDataList() {
        return ruleDetailDataList;
    }

    public void setRuleDetailDataList(List<RuleDetailData> ruleDetailDataList) {
        this.ruleDetailDataList = ruleDetailDataList;
    }
}
