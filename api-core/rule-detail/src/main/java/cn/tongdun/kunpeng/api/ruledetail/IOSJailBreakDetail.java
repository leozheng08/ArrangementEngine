package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

/**
 * 作弊工具识别规则命中详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class IOSJailBreakDetail extends ConditionDetail {

    public IOSJailBreakDetail() {
        super("ios_jail_break");
    }

}
