package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

/**
 * IOS非官方APP识别
 * @Author: liang.chen
 * @Date: 2020/2/6 下午7:54
 */
@Data
public class IOSNotOfficialAppDetail extends NotOfficialAppDetail{

    private String bundleId;

    public IOSNotOfficialAppDetail() {
        super();
    }
}
