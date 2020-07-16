package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import lombok.Data;

/**
 * 设备获取异常
 *
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class FpExceptionDetail extends ConditionDetail implements DetailCallable {

    /**
     * 异常码
     */
    private String code;

    /**
     * 异常码显示名
     */
    private String codeDisplayName;

    private String codeName;

    public FpExceptionDetail() {
        super("fp_exception");
    }


    @Override
    public IDetail call() {
        return this;
    }
}
