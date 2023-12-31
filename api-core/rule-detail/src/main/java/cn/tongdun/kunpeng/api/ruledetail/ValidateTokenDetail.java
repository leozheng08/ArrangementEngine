package cn.tongdun.kunpeng.api.ruledetail;


import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
import lombok.Data;

/**
 * 设备规则-验证码校验详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class ValidateTokenDetail extends ConditionDetail {

    /**
     * 没有传递设备指纹参数
     */
    private Boolean isDeviceIdBlank;
    /**
     * 查无结果、校验失败
     */
    private Boolean isValidFalse;

    public ValidateTokenDetail(){
        super("validate_token");
    }

}
