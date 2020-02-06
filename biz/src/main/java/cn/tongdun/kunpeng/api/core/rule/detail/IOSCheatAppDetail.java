package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

/**
 * 作弊工具识别规则命中详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class IOSCheatAppDetail extends ConditionDetail {

    private String hookInline;

    private String hookIMP;

    public IOSCheatAppDetail() {
        super("ios_cheat_app");
    }


}
