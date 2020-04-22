package cn.tongdun.kunpeng.api.ruledetail;

/**
 * 设备规则-验证码校验详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
public class ValidateTokenDetail extends ConditionDetail {

    //没有传递设备指纹参数
    private Boolean isDeviceIdBlank;
    //查无结果、校验失败
    private Boolean isValidFalse;

    public ValidateTokenDetail(){
        super("validate_token");
    }

}
