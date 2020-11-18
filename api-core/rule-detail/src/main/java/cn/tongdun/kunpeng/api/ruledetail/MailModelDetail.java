package cn.tongdun.kunpeng.api.ruledetail;

import cn.fraudmetrix.module.tdrule.model.IDetail;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import lombok.Data;
import cn.fraudmetrix.module.tdrule.rule.ConditionDetail;
/**
 *
 * @author yuanhang
 *
 * @date 05/27/2020
 *
 */
@Data
public class MailModelDetail extends ConditionDetail implements DetailCallable {

    private String simResult;

    private String randResult;

    private String mail;

    public MailModelDetail() {
        super("mail_model");
    }

    @Override
    public IDetail call() {
        return this;
    }

}
