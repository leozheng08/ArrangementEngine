package cn.tongdun.kunpeng.api.core.rule.detail;

import lombok.Data;

/**
 * 非官方APP识别
 * @Author: liang.chen
 * @Date: 2020/2/6 下午7:54
 */
@Data
public class NotOfficialAppDetail extends ConditionDetail{

    private String bundleId;

    private String packageName;

    public NotOfficialAppDetail() {
        super("not_official_app");
    }
}
