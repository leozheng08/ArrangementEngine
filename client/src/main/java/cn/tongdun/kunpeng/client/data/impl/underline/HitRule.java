package cn.tongdun.kunpeng.client.data.impl.underline;

import cn.tongdun.kunpeng.client.data.IHitRule;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
public class HitRule implements IHitRule {

    private static final long serialVersionUID = 6297666052880082771L;
    private String            id;                                     // 规则编号
    private String            uuid;
    private String            name;                                   // 规则名称
    private String            decision;                               // 该条规则决策结果
    private Integer           score            = 0;                   // 规则分数

    private String            parentUuid;

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

}
