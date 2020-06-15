package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import lombok.Data;

/**
 *
 * @author yuanhang
 *
 * @date 05/27/2020
 *
 */
@Data
public class MailModelDetail extends ConditionDetail implements DetailCallable {

    private String ruleDesc;


    private String simResult;

    private String randResult;

    private String mail;

    @Override
    public IDetail call() {
        return this;
    }

}
