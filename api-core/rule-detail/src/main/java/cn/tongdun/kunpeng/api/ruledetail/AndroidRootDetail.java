package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

/**
 * 安卓root命中详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class AndroidRootDetail extends ConditionDetail{

    public AndroidRootDetail() {
        super("android_root");
    }

}
