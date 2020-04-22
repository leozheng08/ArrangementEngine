package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

/**
 * Android非官方APP识别
 * @Author: liang.chen
 * @Date: 2020/2/6 下午7:54
 */
@Data
public class AndroidNotOfficialAppDetail extends NotOfficialAppDetail{

    private String packageName;

    public AndroidNotOfficialAppDetail() {
        super();
    }

}
