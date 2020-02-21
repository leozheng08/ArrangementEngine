package cn.tongdun.kunpeng.client.data;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
public class HitRule implements Serializable {

    private static final long serialVersionUID = 6297666052880082771L;
    private String            id;                                     // 规则编号
    private String            uuid;
    private String            name;                                   // 规则名称
    private String            decision;                               // 该条规则决策结果
    private Integer           score            = 0;                   // 规则分数

    private String            parentUuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getParentUuid() {
        return parentUuid;
    }

    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

}
