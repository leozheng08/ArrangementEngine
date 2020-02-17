package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import lombok.Data;

/**
 * 作弊工具识别规则命中详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class IOSCheatAppDetail extends ConditionDetail implements DetailCallable {

    private String hookInline;

    private String hookIMP;

    public IOSCheatAppDetail() {
        super("ios_cheat_app");
    }

    @Override
    public IDetail call() {
        return this;
    }
}
