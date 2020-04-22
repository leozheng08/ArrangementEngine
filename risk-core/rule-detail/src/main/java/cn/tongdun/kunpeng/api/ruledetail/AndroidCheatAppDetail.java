package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import lombok.Data;

import java.util.*;


/**
 * 作弊工具识别规则命中详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class AndroidCheatAppDetail extends ConditionDetail implements DetailCallable {

    private String hookMethod;
    private String hookInline;
    private String hookAddress;

    /**
     * 安装作弊工具
     *
     * http://wiki.tongdun.me/pages/viewpage.action?pageId=30552343
     */
    private List<String> installedDangerApps;

    /**
     * 运行作弊工具
     *
     * http://wiki.tongdun.me/pages/viewpage.action?pageId=30552343
     */
    private List<String> runningDangerApps;

    public AndroidCheatAppDetail() {
        super("android_cheat_app");
    }


    @Override
    public IDetail call() {
        return this;
    }
}
