package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

@Data
public class MatchAddressDetail extends ConditionDetail {


    private String addressA;
    private String addressAValue;
    private String addressADisplayName;
    private String addressB;
    private String addressBValue;
    private String addressBDisplayName;

    public MatchAddressDetail(){
        super("match_address");
    }
}
