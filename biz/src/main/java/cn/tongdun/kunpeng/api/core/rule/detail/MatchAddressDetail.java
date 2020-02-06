package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

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
