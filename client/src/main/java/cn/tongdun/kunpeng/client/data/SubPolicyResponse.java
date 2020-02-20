package cn.tongdun.kunpeng.client.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 下午4:44
 */
@Data
public class SubPolicyResponse extends Response{

    private String subPolicyUuid;
    private String subPolicyName;
    private String policyUuid;
    private String policyName;
    private String riskType;
    private PolicyMode policyMode;

    private List<HitRule> hitRules = new ArrayList<>();

    public void addHitRule(HitRule hitRule){
        hitRules.add(hitRule);
    }
}
