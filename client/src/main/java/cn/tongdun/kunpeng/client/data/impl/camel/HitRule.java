package cn.tongdun.kunpeng.client.data.impl.camel;

import cn.tongdun.kunpeng.client.data.IHitRule;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
public class HitRule implements IHitRule {

    private static final long serialVersionUID = 6297666052880082771L;
    private String id;                                     // 规则编号
    private String uuid;
    private String name;                                   // 规则名称
    private String decision;                               // 该条规则决策结果
    private Integer score = 0;                   // 规则分数

    private String parentUuid;

    //场景化中，规则标签输出
    private List<String> businessTag;

    public List<String> getBusinessTag() {
        return businessTag;
    }

    public void setBusinessTag(List<String> businessTag) {
        this.businessTag = businessTag;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDecision() {
        return decision;
    }

    @Override
    public void setDecision(String decision) {
        this.decision = decision;
    }

    @Override
    public Integer getScore() {
        return score;
    }

    @Override
    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getParentUuid() {
        return parentUuid;
    }

    @Override
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    @Override
    public String getEnName() {
        return null;
    }

    @Override
    public void setEnName(String enName) {

    }

    @Override
    public String getDealType() {
        return null;
    }

    @Override
    public void setDealType(String dealType) {

    }


}
