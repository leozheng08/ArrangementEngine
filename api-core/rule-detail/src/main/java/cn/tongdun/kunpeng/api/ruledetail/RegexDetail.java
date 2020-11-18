package cn.tongdun.kunpeng.api.ruledetail;


import lombok.Data;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;

/**
 * 正则配置详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午9:53
 */
@Data
public class RegexDetail extends ConditionDetail {

    private String dimType;
    private String dimTypeDisplayName;
    private String value;

    public RegexDetail(){
        super("regex");
    }
}
