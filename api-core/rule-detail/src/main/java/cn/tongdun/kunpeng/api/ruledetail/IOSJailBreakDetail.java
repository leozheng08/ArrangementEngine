package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
/**
 * ios越狱识别命中详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class IOSJailBreakDetail extends ConditionDetail {

    public IOSJailBreakDetail() {
        super("ios_jail_break");
    }

}
