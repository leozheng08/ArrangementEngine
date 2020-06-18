package cn.tongdun.kunpeng.client.data.impl.us;

import cn.tongdun.kunpeng.client.data.IHitRule;

/**
 * @author: yuanhang
 * @date: 2020-06-17 15:52
 **/
public class USHitRule implements IHitRule {

    private String uuid;

    private String name;

    private String decision;

    private Integer score;

    private String enName;

    private String dealType;

    private String parentUuid;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDecision() {
        return this.decision;
    }

    @Override
    public void setDecision(String decision) {
        this.decision = decision;
    }

    @Override
    public Integer getScore() {
        return this.score;
    }

    @Override
    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String getParentUuid() {
        return this.parentUuid;
    }

    @Override
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    @Override
    public String getEnName() {
        return enName;
    }

    @Override
    public void setEnName(String enName) {
        this.enName = enName;
    }

    @Override
    public String getDealType() {
        return dealType;
    }

    @Override
    public void setDealType(String dealType) {
        this.dealType = dealType;
    }
}
